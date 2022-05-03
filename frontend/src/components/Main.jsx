import React, { Suspense, useEffect, useState } from 'react';
import clsx from 'clsx';

import { withStyles } from '@material-ui/core/styles';

import { getSession, removeAuthenticator } from '../api';

import CreateUser from './CreateUser';
import PageLoadingSpinner from './PageLoadingSpinner';
import Register from './Register';
import Signin from './Signin';
import UserMainPage from './UserMainPage';

import backgroundStandard from '../img/background-yks.jpg';


const styles = theme => ({
  root: {
    height: '100%',
    paddingBottom: theme.spacing(6),
    paddingTop: theme.spacing(6),
    [theme.breakpoints.up('md')]: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      overflowY: 'auto',
      background: `url(${backgroundStandard}) no-repeat center center fixed`,
      backgroundSize: 'cover',
    },
    backgroundColor: '#f1f1f1',
  },
  '@media all and (-ms-high-contrast: active), (-ms-high-contrast: none)': {
    root: {
      flex: '1 1 auto',
    },
  },
});

function Main({ classes }) {
  let comp = null;

  const [state, setState] = useState('signin');
  const [sessionInfo, setSessionInfo] = useState(null);

  const refreshSession = () => {
    getSession()
      .then(data => setSessionInfo({
        username: data.username,
        userId: data.userId,
        credentials: data.credentials,
      }))
      .catch(() => setSessionInfo(null));
  };
  useEffect(refreshSession, [sessionInfo?.userId]);

  const onDeleteCredential = id => {
    removeAuthenticator(id)
      .finally(() => refreshSession());
  };


  const loggedIn = sessionInfo !== null;

  if (state === 'register') {
    comp = (
      <Register
        onAbort={() => setState(null)}
        onSuccess={() => {
          refreshSession();
          setState(null);
        }}
      />
    );

  } else if (loggedIn) {
    comp = (
      <UserMainPage
        sessionInfo={sessionInfo}
        onAddCredential={() => setState('register')}
        onDeleteCredential={onDeleteCredential}
        onLogout={refreshSession}
      />
    );

  } else if (state === 'create-user') {
    comp = (
      <CreateUser
        onShowSignin={() => setState('signin')}
        onSuccess={data => setSessionInfo({
          username: data.username,
          userId: data.userId,
        })}
      />
    );

  } else {
    comp = (
      <Signin
        onShowRegistration={() => setState('create-user')}
        onSuccess={data => setSessionInfo({
          username: data.username,
          userId: data.userId,
        })}
      />
    );
  }

  return (
    <div className={classes.root}>
      <Suspense fallback={<PageLoadingSpinner />}>
        {comp}
      </Suspense>
    </div>
  );
}

export default withStyles(styles)(Main);
