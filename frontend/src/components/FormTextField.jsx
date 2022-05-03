import React from 'react';

import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';

const styles = theme => ({
  textField: {
    marginLeft: theme.spacing(1),
    marginRight: theme.spacing(1),
    width: 200,
  },
  error: { position: 'absolute', bottom: '-0.85rem' },
});

function FormTextField({ classes, ...props }) {
  return (
    <TextField
      {...props}
      helperText={getHelperText(props.helperText)}
      FormHelperTextProps={{
        classes: {
          root: classes.error,
        },
      }}
    />
  );
}

export default withStyles(styles)(FormTextField);

function getHelperText(errorField) {
  if (typeof errorField === 'string' && errorField.length > 0) {
    return errorField;
  }

  return null;
}
