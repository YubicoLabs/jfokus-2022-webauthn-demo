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
  clickableText: {
    cursor: 'pointer',
    display: 'inline-block',
    textDecoration: 'underline',
    color: theme.palette.primary.main,
  },
});


function RegisterForm({
  onSubmit,
  submitting = false,
  classes,
  onShowSignin,
}) {

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = event => {
    event.preventDefault();
    onSubmit(username, password);
  };

  return (
    <form className={classes.form} onSubmit={handleSubmit}>
      <div>
        <FormTextField
          autoComplete="off"
          inputProps={{
            autoCapitalize: 'none',
            autoCorrect: 'off',
          }}
          fullWidth
          id="username"
          label="Username"
          margin="normal"
          name="username"
          onChange={event => setUsername(event.target.value)}
          required
          value={username}
        />

        <FormTextField
          autoComplete="off"
          inputProps={{
            autoCapitalize: 'none',
            autoCorrect: 'off',
          }}
          fullWidth
          id="pwd"
          label="Password"
          margin="normal"
          name="pwd"
          onChange={event => setPassword(event.target.value)}
          required
          type="password"
          value={password}
        />

        <Typography
          className={classes.pageSwitcherText}
          color="textSecondary"
          paragraph={true}
        >
          I already have an account.{' '}
          <span className={classes.clickableText} onClick={onShowSignin}>
            Sign in
          </span>
          .
        </Typography>
      </div>

      <div className={classes.buttons}>
        <Button
          color="primary"
          disabled={submitting}
          fullWidth={true}
          type="submit"
          variant="contained"
        >
          Register
        </Button>
      </div>
    </form>
  );
}

export default withStyles(styles)(RegisterForm);
