import React from 'react';

import { withStyles } from '@material-ui/core/styles';
import CircularProgress from '@material-ui/core/CircularProgress';

const styles = theme => ({
  loader: {
    height: '100%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#f1f1f1',
  },
});

const PageLoadingSpinner = ({ classes }) => (
  <div className={classes.loader}>
    <CircularProgress disableShrink={true} />
  </div>
);

export default withStyles(styles)(PageLoadingSpinner);
