import React, { useState, useEffect } from 'react';
import userService from '../../services/user.service';
import Button from '../common/Button';
import Input from '../common/Input';

const RoleEditor = ({ user, onRolesUpdated, onCancel }) => {
  const [selectedRoles, setSelectedRoles] = useState({
    ROLE_USER: false,
    ROLE_ADMIN: false
  });
  const [updateMessage, setUpdateMessage] = useState({ type: '', message: '' });
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (user?.roles) {
      setSelectedRoles({
        ROLE_USER: user.roles.includes('ROLE_USER'),
        ROLE_ADMIN: user.roles.includes('ROLE_ADMIN')
      });
    }
  }, [user]);

  const handleRoleChange = (role) => {
    setSelectedRoles(prev => ({
      ...prev,
      [role]: !prev[role]
    }));
  };

  const handleSubmit = async () => {
    try {
      setIsSubmitting(true);
      const roles = Object.entries(selectedRoles)
        .filter(([_, isSelected]) => isSelected)
        .map(([role]) => role);

      const result = await userService.updateUserRoles(user.username, roles);
      
      if (result.success) {
        setUpdateMessage({ type: 'success', message: 'Roles updated successfully' });
        onRolesUpdated(roles);
      } else {
        setUpdateMessage({ type: 'error', message: result.message });
      }
    } catch (err) {
      setUpdateMessage({ 
        type: 'error', 
        message: err.response?.data?.data?.message || 'Failed to update roles' 
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="space-y-4">
      {updateMessage.message && (
        <div className={`px-4 py-3 rounded ${
          updateMessage.type === 'success' 
            ? 'bg-green-50 text-green-800' 
            : 'bg-red-50 text-red-800'
        }`}>
          {updateMessage.message}
        </div>
      )}

      <div className="space-y-2">
        <Input
          type="checkbox"
          id="roleUser"
          name="roleUser"
          label="User"
          checked={selectedRoles.ROLE_USER}
          onChange={() => handleRoleChange('ROLE_USER')}
          disabled={isSubmitting}
          className="checkbox-input"
        />
        <Input
          type="checkbox"
          id="roleAdmin"
          name="roleAdmin"
          label="Admin"
          checked={selectedRoles.ROLE_ADMIN}
          onChange={() => handleRoleChange('ROLE_ADMIN')}
          disabled={isSubmitting}
          className="checkbox-input"
        />
      </div>

      <div className="flex space-x-3">
        <Button
          onClick={handleSubmit}
          disabled={isSubmitting}
          loading={isSubmitting}
          variant="success"
        >
          Save Changes
        </Button>
        <Button
          onClick={onCancel}
          disabled={isSubmitting}
          variant="secondary"
        >
          Cancel
        </Button>
      </div>
    </div>
  );
};

export default RoleEditor; 