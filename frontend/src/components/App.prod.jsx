import React from 'react';

import { withStyles } from '@material-ui/core/styles';

import withRoot from '../styles/withRoot';

import Main from './Main';

const styles = theme => ({
  root: {
    display: 'flex',
    flexDirection: 'column',
    height: '100%',
  },
});

function App({ classes }) {
  return (
    <div className={classes.root}>
      <Main />
    </div>
  );
}

export default withRoot(withStyles(styles)(App));
