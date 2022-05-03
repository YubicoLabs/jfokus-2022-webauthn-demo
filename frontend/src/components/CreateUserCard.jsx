import React from 'react';

import CreateUserForm from './CreateUserForm';
import {
  GenericCardHeader,
  GenericCard,
  GenericCardContent,
} from './GenericCard';

export default function CreateUserCard({
  onSubmit,
  onShowSignin,
}) {
  return (
    <GenericCard>
      <GenericCardHeader text="Create account" />

      <GenericCardContent>
        <CreateUserForm
          onSubmit={onSubmit}
          onShowSignin={onShowSignin}
        />
      </GenericCardContent>
    </GenericCard>
  );
}
