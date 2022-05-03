import React, { useEffect, useState } from 'react';
import * as webauthnJson from "@github/webauthn-json";

import { withStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import CircularProgress from '@material-ui/core/CircularProgress';

import { register, registerFinish } from '../api';

import VerifyCard from './VerifyCard.jsx';
import {
  GenericCardHeader,
  GenericCard,
  GenericCardContent,
} from './GenericCard';

const styles = theme => ({
  buttons: {
    marginTop: theme.spacing(14),
    display: 'flex',
    justifyContent: 'center',
    [theme.breakpoints.down('xs')]: {
      marginTop: theme.spacing(10),
    },
  },
  button: {
    margin: theme.spacing(2),
  },
  root: {
    [theme.breakpoints.down('sm')]: {
      height: '100%',
      display: 'flex',
    },
  },
});


function Register({
  classes,
  onAbort,
  onSuccess,
}) {
  const [submitting, setSubmitting] = useState(false);
  const [webauthnInProgress, setWebauthnInProgress] = useState(true);

  const finishRegistration = (data, pkc) => {
    registerFinish(data, pkc)
      .then(onSuccess);
  };

  const runRegistration = () => {
    setSubmitting(true);
    register()
      .finally(() => setSubmitting(false))
      .then(data => {
        setWebauthnInProgress(true);
        if (data.userId) {
          return data;
        } else if (data.request) {
          return webauthnJson.create({ publicKey: data.request.publicKeyCredentialCreationOptions })
            .then(pkc => finishRegistration(data, pkc))
            .finally(() => setWebauthnInProgress(false));
        } else {
          throw new Error('Unknown registration result');
        }
      });
  };

  useEffect(runRegistration, []);

  let comp = null;
  if (webauthnInProgress) {
    comp = (
      <GenericCard>
        <GenericCardContent>
          <VerifyCard title="Interact with your authenticator">
            <div className={classes.buttons}>
              <CircularProgress color="primary" />
            </div>
          </VerifyCard>
        </GenericCardContent>
      </GenericCard>
    );

  } else {
    comp = (
      <GenericCard>
        <GenericCardHeader text="Oops!" />

        <GenericCardContent>
          Something went wrong, please try again.

          <div className={classes.buttons}>
            <Button
              className={classes.button}
              color="default"
              disabled={webauthnInProgress}
              type="button"
              variant="contained"
              onClick={onAbort}
            >
              Go back
            </Button>

            <Button
              className={classes.button}
              color="primary"
              disabled={webauthnInProgress}
              type="button"
              variant="contained"
              onClick={runRegistration}
            >
              Try again
            </Button>
          </div>
        </GenericCardContent>
      </GenericCard>
    );
  }

  return (
    <div className={classes.root}>
      {comp}
    </div>
  );
}
export default withStyles(styles)(Register);
