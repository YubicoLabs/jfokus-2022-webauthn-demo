import React from 'react';

import RegisterForm from './RegisterForm';
import {
  GenericCardHeader,
  GenericCard,
  GenericCardContent,
} from './GenericCard';

export default function RegisterCard({
  onSubmit,
  onShowSignin,
}) {
  return (
    <GenericCard>
      <GenericCardHeader text="Create account" />

      <GenericCardContent>
        <RegisterForm
          onSubmit={onSubmit}
          onShowSignin={onShowSignin}
        />
      </GenericCardContent>
    </GenericCard>
  );
}
