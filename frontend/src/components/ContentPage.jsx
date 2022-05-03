import React from 'react';

import { withStyles } from '@material-ui/core/styles';

const styles = theme => ({
  root: {
    height: '100%',
    width: '60%',
    overflowY: 'auto',
    backgroundColor: '#ffffff',
    borderLeft: 'solid 1px #aaaaaa',
    borderRight: 'solid 1px #aaaaaa',
    padding: theme.spacing(10),
    paddingBottom: theme.spacing(4),
    paddingTop: theme.spacing(4),
    [theme.breakpoints.down('sm')]: {
      width: '100%',
      borderLeft: 'unset',
      borderRight: 'unset',
      padding: 'unset',
      paddingBottom: theme.spacing(2),
      paddingTop: theme.spacing(4),
      ...theme.mixins.gutters(),
    },
  },
});

function ContentPage({ classes, children }) {
  return <div className={classes.root}>{children}</div>;
}

export default withStyles(styles)(ContentPage);
