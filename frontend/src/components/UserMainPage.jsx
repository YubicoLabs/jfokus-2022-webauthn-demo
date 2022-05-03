import React, { useEffect, useState } from 'react';
import clsx from 'clsx';

import { withStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import CircularProgress from '@material-ui/core/CircularProgress';
import Typography from '@material-ui/core/Typography';

import { logout } from '../api';

import { SecurityKeys } from './Authenticator';
import ContentPage from './ContentPage';


const styles = theme => ({
  logoutButton: {
    textAlign: 'center',
  },
  title: {
    margin: theme.spacing(2),
    marginTop: 0,
  },
  contentMarginTop: {
    marginTop: theme.spacing(4),
  },
  innerContent: {
    [theme.breakpoints.up('md')]: {
      paddingLeft: theme.spacing(2),
      paddingRight: theme.spacing(2),
    },
  },
});


function UserMainPage({
  classes,
  sessionInfo: { username, userId, credentials = []},
  onAddCredential,
  onLogout,
}) {
  const [enrollModalOpened, setEnrollModalOpened] = useState(false);
  const [loggingOut, setLoggingOut] = useState(false);

  const handleLogout = () => {
    setLoggingOut(true);
    logout()
      .then(onLogout)
      .finally(() => setLoggingOut(false));
  };

  return (
    <ContentPage>
      <Typography
        className={classes.title}
        color="secondary"
        align="center"
        variant="h4"
      >
        <strong>User: {username}</strong>
      </Typography>

      <div className={clsx(classes.innerContent)}>
        <SecurityKeys
          authenticators={credentials}
          onAdd={onAddCredential}
        />
      </div>

      <br/>

      <div className={classes.logoutButton}>
        <Button
          color="primary"
          disabled={loggingOut}
          type="button"
          variant="contained"
          onClick={handleLogout}
        >
          Log out
        </Button>
      </div>
    </ContentPage>
  );
}
export default withStyles(styles)(UserMainPage);
