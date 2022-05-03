import React, { useState } from 'react';
import * as webauthnJson from "@github/webauthn-json";

import { withStyles } from '@material-ui/core/styles';
import CircularProgress from '@material-ui/core/CircularProgress';

import SigninForm from './SigninForm';
import {
  GenericCardHeader,
  GenericCard,
  GenericCardContent,
} from './GenericCard';

import VerifyCard from './VerifyCard';

import { login, verifyFinish } from '../api';


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
  const [webauthnInProgress, setWebauthnInProgress] = useState(false);

  const handleSubmit = (username, password) => {
    setSubmitting(true);
    login(username, password)
      .finally(() => setSubmitting(false))
      .then(data => {
        setWebauthnInProgress(true);
        if (data.userId) {
          return data;
        } else if (data.request) {
          return webauthnJson.get({ publicKey: data.request.publicKeyCredentialRequestOptions })
            .then(pkc => verifyFinish(data, pkc))
            .finally(() => setWebauthnInProgress(false));
        } else {
          throw new Error('Unknown login result');
        }
      })
      .then(onSuccess);
  };

  let comp = (
    <SigninForm
      onShowRegistration={onShowRegistration}
      onSubmit={handleSubmit}
      submitting={submitting}
    />
  );
  if (webauthnInProgress) {
    comp = (
      <VerifyCard title="Interact with your authenticator">
        <div className={classes.buttons}>
          <CircularProgress color="primary" />
        </div>
      </VerifyCard>
    );
  }

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
