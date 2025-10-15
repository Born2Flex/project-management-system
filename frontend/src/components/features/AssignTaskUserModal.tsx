import React, { useState } from 'react';
import { useDevelopers } from '@/hooks/useProjects';
import Button from '@/components/common/Button';

interface AssignTaskUserModalProps {
  isOpen: boolean;
  onClose: () => void;
  projectId: number;
  onAssign: (userId: number) => void;
  isAssigning?: boolean;
}

export const AssignTaskUserModal: React.FC<AssignTaskUserModalProps> = ({ 
  isOpen, 
  onClose, 
  projectId, 
  onAssign,
  isAssigning = false 
}) => {
  const { developers, isLoading } = useDevelopers(projectId);
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedUserId) {
      onAssign(selectedUserId);
      setSelectedUserId(null);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-md mx-4">
        <div className="p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-2xl font-bold text-gray-900">Assign Task</h2>
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
            <div>
              <label htmlFor="assignee" className="block text-sm font-medium text-gray-700 mb-1">
                Select Assignee <span className="text-red-500">*</span>
              </label>
              <select
                id="assignee"
                value={selectedUserId || ''}
                onChange={(e) => setSelectedUserId(Number(e.target.value))}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                disabled={isLoading}
                required
              >
                <option value="">Select a developer...</option>
                {developers.map((developer) => (
                  <option key={developer.id} value={developer.id}>
                    {developer.name} ({developer.email})
                  </option>
                ))}
              </select>
              {developers.length === 0 && !isLoading && (
                <p className="mt-2 text-sm text-gray-500">No developers available in this project.</p>
              )}
            </div>

            <div className="flex gap-3 pt-4">
              <Button
                type="button"
                variant="secondary"
                onClick={onClose}
                className="flex-1"
                disabled={isAssigning}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="primary"
                className="flex-1"
                disabled={isAssigning || !selectedUserId || developers.length === 0}
              >
                {isAssigning ? 'Assigning...' : 'Assign Task'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AssignTaskUserModal;

