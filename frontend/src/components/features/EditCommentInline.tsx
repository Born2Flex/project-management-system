import React, { useState, useEffect, useRef } from 'react';
import { useComments } from '@/hooks/useComments';
import Button from '@/components/common/Button';
import { type Comment } from '@/types/comment.types';

interface EditCommentInlineProps {
  comment: Comment;
  projectId: number;
  taskId: number;
  onCancel: () => void;
  onSuccess: () => void;
}

export const EditCommentInline: React.FC<EditCommentInlineProps> = ({
  comment,
  projectId,
  taskId,
  onCancel,
  onSuccess,
}) => {
  const { updateComment, isUpdating } = useComments(projectId, taskId);
  const [text, setText] = useState(comment.text);
  const [error, setError] = useState('');
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.focus();
      textareaRef.current.setSelectionRange(text.length, text.length);
    }
  }, [text.length]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!text.trim()) {
      setError('Comment text is required');
      return;
    }

    try {
      await updateComment({
        id: comment.id,
        data: { text: text.trim() }
      });
      onSuccess();
    } catch (error) {
      console.error('Failed to update comment:', error);
      setError('Failed to update comment');
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Escape') {
      onCancel();
    } else if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
      handleSubmit(e);
    }
  };

  return (
    <div className="border border-blue-300 rounded-lg p-3 bg-blue-50">
      <form onSubmit={handleSubmit} className="space-y-3">
        <textarea
          ref={textareaRef}
          value={text}
          onChange={(e) => {
            setText(e.target.value);
            setError('');
          }}
          onKeyDown={handleKeyDown}
          rows={3}
          className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none ${
            error ? 'border-red-500' : 'border-gray-300'
          }`}
          placeholder="Edit your comment..."
          disabled={isUpdating}
        />
        
        {error && (
          <p className="text-sm text-red-600">{error}</p>
        )}

        <div className="flex gap-2 justify-end">
          <Button
            type="button"
            variant="secondary"
            size="sm"
            onClick={onCancel}
            disabled={isUpdating}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            size="sm"
            disabled={isUpdating || !text.trim()}
            isLoading={isUpdating}
          >
            {isUpdating ? 'Updating...' : 'Update'}
          </Button>
        </div>
      </form>
      
      <div className="text-xs text-gray-500 mt-2">
        Press Ctrl+Enter to save, Escape to cancel
      </div>
    </div>
  );
};

export default EditCommentInline;
