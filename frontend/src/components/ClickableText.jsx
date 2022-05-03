import React from 'react';

import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';

const styles = theme => ({
  clickableText: {
    cursor: 'pointer',
    display: 'inline-block',
    marginTop: theme.spacing(2),
    '&:hover': {
      textDecoration: 'underline',
    },
  },
});

function ClickableText({
  classes,
  children,
  color = 'textSecondary',
  paragraph = false,
  onClick = () => {},
}) {
  return (
    <Typography
      color={color}
      className={classes.clickableText}
      onClick={onClick}
      paragraph={paragraph}
    >
      {children}
    </Typography>
  );
}

export default withStyles(styles)(ClickableText);
