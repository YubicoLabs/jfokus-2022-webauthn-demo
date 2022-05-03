import React, { useState } from 'react';

import { withStyles } from '@material-ui/core/styles';
import CircularProgress from '@material-ui/core/CircularProgress';

import SigninForm from './SigninForm';
import {
  GenericCardHeader,
  GenericCard,
  GenericCardContent,
} from './GenericCard';

import { login } from '../api';


const styles = theme => ({
  buttons: {
    marginTop: theme.spacing(14),
    display: 'flex',
    justifyContent: 'center',
    [theme.breakpoints.down('xs')]: {
      marginTop: theme.spacing(10),
    },
  },
  root: {
    [theme.breakpoints.down('sm')]: {
      height: '100%',
      display: 'flex',
    },
  },
});


function Signin({
  classes,
  onShowRegistration,
  onSuccess,
}) {
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = (username, password) => {
    setSubmitting(true);
    login(username, password)
      .finally(() => setSubmitting(false))
      .then(onSuccess);
  };

  const comp = (
    <SigninForm
      onShowRegistration={onShowRegistration}
      onSubmit={handleSubmit}
      submitting={submitting}
    />
  );

  return (
    <div className={classes.root}>
      <GenericCard>
        <GenericCardHeader text="Sign in" />

        <GenericCardContent>{comp}</GenericCardContent>
      </GenericCard>
    </div>
  );
}
export default withStyles(styles)(Signin);
