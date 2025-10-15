import React, { useState } from 'react';
import { useUsers } from '@/hooks/useUsers';
import { useDevelopers } from '@/hooks/useProjects';
import Button from '@/components/common/Button';

interface AssignUserModalProps {
  isOpen: boolean;
  onClose: () => void;
  projectId: number;
  onAssign: (userId: number) => void;
  isAssigning?: boolean;
}

export const AssignUserModal: React.FC<AssignUserModalProps> = ({ 
  isOpen, 
  onClose, 
  projectId, 
  onAssign,
  isAssigning = false 
}) => {
  const { users, isLoading: isLoadingUsers } = useUsers();
  const { developers, isLoading: isLoadingDevelopers } = useDevelopers(projectId);
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);

  const developerIds = new Set(developers.map(d => d.id));
  
  const availableUsers = users.filter(user => !developerIds.has(user.id));

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
            <h2 className="text-2xl font-bold text-gray-900">Add Developer to Project</h2>
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
              <label htmlFor="user" className="block text-sm font-medium text-gray-700 mb-1">
                Select User <span className="text-red-500">*</span>
              </label>
              <select
                id="user"
                value={selectedUserId || ''}
                onChange={(e) => setSelectedUserId(Number(e.target.value))}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                disabled={isLoadingUsers || isLoadingDevelopers}
                required
              >
                <option value="">Select a user...</option>
                {availableUsers.map((user) => (
                  <option key={user.id} value={user.id}>
                    {user.name} ({user.email})
                  </option>
                ))}
              </select>
              {availableUsers.length === 0 && !isLoadingUsers && !isLoadingDevelopers && (
                <p className="mt-2 text-sm text-gray-500">All users are already developers in this project.</p>
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
                disabled={isAssigning || !selectedUserId || availableUsers.length === 0}
              >
                {isAssigning ? 'Adding...' : 'Add Developer'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AssignUserModal;

