import React from 'react';

import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';

const styles = theme => ({
  verifyMainText: {
    textAlign: 'center',
  },
});

function VerifyCard({ classes, children, title }) {
  return (
    <React.Fragment>
      {title && (
        <Typography
          color="textSecondary"
          variant="h5"
          className={classes.verifyMainText}
        >
          {title}
        </Typography>
      )}
      {children}
    </React.Fragment>
  );
}

export default withStyles(styles)(VerifyCard);
