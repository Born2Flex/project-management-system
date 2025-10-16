import React, { useState, useEffect } from 'react';
import { useTasks } from '@/hooks/useTasks';
import { TaskStatus, TaskPriority, type Task } from '@/types/task.types';
import Button from '@/components/common/Button';
import Input from '@/components/common/Input';

interface EditTaskModalProps {
  isOpen: boolean;
  onClose: () => void;
  projectId: number;
  task: Task;
}

export const EditTaskModal: React.FC<EditTaskModalProps> = ({ isOpen, onClose, projectId, task }) => {
  const { updateTask, isUpdating } = useTasks(projectId);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    status: TaskStatus.OPEN,
    priority: TaskPriority.MEDIUM,
    dueDateTime: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (task && isOpen) {
      setFormData({
        title: task.title,
        description: task.description,
        status: task.status,
        priority: task.priority,
        dueDateTime: task.dueDateTime ? new Date(task.dueDateTime).toISOString().slice(0, 16) : '',
      });
      setErrors({});
    }
  }, [task, isOpen]);

  const handleChange = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    setErrors((prev) => ({ ...prev, [field]: '' }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const newErrors: Record<string, string> = {};

    if (!formData.title.trim()) {
      newErrors.title = 'Task title is required';
    }

    if (!formData.description.trim()) {
      newErrors.description = 'Description is required';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    try {
      await updateTask({
        taskId: task.id,
        data: {
          title: formData.title.trim(),
          description: formData.description.trim(),
          status: formData.status,
          priority: formData.priority,
          dueDateTime: formData.dueDateTime || undefined,
        }
      });
      onClose();
    } catch (error) {
      console.error('Failed to update task:', error);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-md mx-4">
        <div className="p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-2xl font-bold text-gray-900">Edit Task</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 transition-colors"
              aria-label="Close"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="Task Title"
              id="title"
              value={formData.title}
              onChange={(e) => handleChange('title', e.target.value)}
              error={errors.title}
              required
              placeholder="Enter task title"
            />

            <div>
              <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-1">
                Description <span className="text-red-500">*</span>
              </label>
              <textarea
                id="description"
                value={formData.description}
                onChange={(e) => handleChange('description', e.target.value)}
                rows={4}
                className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  errors.description ? 'border-red-500' : 'border-gray-300'
                }`}
                placeholder="Enter task description"
              />
              {errors.description && (
                <p className="mt-1 text-sm text-red-600">{errors.description}</p>
              )}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label htmlFor="status" className="block text-sm font-medium text-gray-700 mb-1">
                  Status
                </label>
                <select
                  id="status"
                  value={formData.status}
                  onChange={(e) => handleChange('status', e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value={TaskStatus.OPEN}>Open</option>
                  <option value={TaskStatus.IN_PROGRESS}>In Progress</option>
                  <option value={TaskStatus.UNDER_REVIEW}>Under Review</option>
                  <option value={TaskStatus.COMPLETED}>Completed</option>
                </select>
              </div>

              <div>
                <label htmlFor="priority" className="block text-sm font-medium text-gray-700 mb-1">
                  Priority
                </label>
                <select
                  id="priority"
                  value={formData.priority}
                  onChange={(e) => handleChange('priority', e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value={TaskPriority.LOW}>Low</option>
                  <option value={TaskPriority.MEDIUM}>Medium</option>
                  <option value={TaskPriority.HIGH}>High</option>
                  <option value={TaskPriority.CRITICAL}>Critical</option>
                </select>
              </div>
            </div>

            <Input
              label="Due Date"
              id="dueDateTime"
              type="datetime-local"
              value={formData.dueDateTime}
              onChange={(e) => handleChange('dueDateTime', e.target.value)}
              placeholder="Select due date"
            />

            <div className="flex gap-3 pt-4">
              <Button
                type="button"
                variant="secondary"
                onClick={onClose}
                className="flex-1"
                disabled={isUpdating}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="primary"
                className="flex-1"
                disabled={isUpdating}
                isLoading={isUpdating}
              >
                {isUpdating ? 'Updating...' : 'Update Task'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default EditTaskModal;
