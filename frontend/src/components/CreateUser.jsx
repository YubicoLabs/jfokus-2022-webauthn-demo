import React, { useState } from 'react';

import { withStyles } from '@material-ui/core/styles';

import { createUser } from '../api';

import CreateUserCard from './CreateUserCard.jsx';

const styles = theme => ({
  root: {
    [theme.breakpoints.down('sm')]: {
      height: '100%',
      display: 'flex',
      flexDirection: 'column',
    },
  },
});


function CreateUser({
  classes,
  onShowSignin,
  onSuccess,
}) {
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = (username, password) => {
    setSubmitting(true);
    createUser(username, password)
      .finally(() => setSubmitting(false))
      .then(data => {
        onShowSignin();
        onSuccess(data);
      });
  };

  return (
    <div className={classes.root}>
      <CreateUserCard
        onSubmit={handleSubmit}
        onShowSignin={onShowSignin}
      />
    </div>
  );
}
export default withStyles(styles)(CreateUser);
