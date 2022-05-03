import React, { useState } from 'react';

import { withStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';

import FormTextField from './FormTextField';


const styles = theme => ({
  buttons: {
    marginTop: theme.spacing(10),
  },
  form: {
    height: '100%',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'space-between',
  },
  pageSwitcherText: {
    marginTop: theme.spacing(2),
  },
});


function RegisterForm({
  onSubmit,
  submitting = false,
  classes,
}) {

  const [nickname, setNickname] = useState('');

  const handleSubmit = event => {
    event.preventDefault();
    onSubmit(nickname);
  };

  return (
    <form className={classes.form} onSubmit={handleSubmit}>
      <div>
        <Typography
          className={classes.pageSwitcherText}
          color="textSecondary"
          paragraph={true}
        >
          Would you like to set a nickname for this credential?
        </Typography>

        <FormTextField
          autoComplete="off"
          inputProps={{
            autoCapitalize: 'none',
            autoCorrect: 'off',
          }}
          fullWidth
          id="nickname"
          label="Nickname"
          margin="normal"
          name="nickname"
          onChange={event => setNickname(event.target.value)}
          value={nickname}
        />
      </div>

      <div className={classes.buttons}>
        <Button
          color="primary"
          disabled={submitting}
          fullWidth={true}
          type="submit"
          variant="contained"
        >
          Finish
        </Button>
      </div>
    </form>
  );
}

export default withStyles(styles)(RegisterForm);
