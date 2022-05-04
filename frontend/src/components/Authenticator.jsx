import React from 'react';
import dayjs from 'dayjs';

import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';

import Checked from '@material-ui/icons/CheckCircle';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';


const styles = theme => ({
  root: {
    paddingTop: theme.spacing(2),
    paddingLeft: theme.spacing(6),
    paddingRight: theme.spacing(3),
  },
  authenticatorContainer: {
    width: '100%',
    display: 'flex',
    flexDirection: 'row',
  },
  authenticator: {
    display: 'flex',
    justifyContent: 'space-between',
    marginLeft: theme.spacing(2),
    marginBottom: theme.spacing(2),
    width: '100%',
  },
  expansionPanelDetailsRoot: {
    flexDirection: 'column',
    alignItems: 'flex-start',
  },
  passwordless: {
    width: '0.6em',
    height: '0.6em',
    position: 'relative',
    top: '2px',
  },
  noAuth: {
    marginLeft: theme.spacing(2),
    marginBottom: theme.spacing(2),
  },
});


function AuthenticatorComp({
  classes,
  createTime,
  lastUseTime,
}) {
  return (
    <div className={classes.authenticatorContainer}>
      <div className={classes.authenticator}>
        <div>
          <Typography color="textSecondary" variant="caption" component="p">
            Last used: {dayjs(lastUseTime).format('MMMM DD YYYY, h:mm A')}
          </Typography>
        </div>
      </div>
    </div>
  );
}

const Authenticator = withStyles(styles)(AuthenticatorComp);

function AuthenticatorsComp({
  classes,
  expanded = true,
  authenticators,
  title,
  onAdd,
}) {
  return (
    <ExpansionPanel defaultExpanded={expanded}>
      <ExpansionPanelSummary
        expandIcon={<ExpandMoreIcon />}
        classes={{ root: classes.expansionPanelSummaryRoot }}
      >
        {title}
      </ExpansionPanelSummary>

      <ExpansionPanelDetails
        classes={{ root: classes.expansionPanelDetailsRoot }}
      >
        {authenticators.length > 0 ? (
          authenticators.map(auth => (
            <Authenticator
              key={auth.id}
              {...auth}
            />
          ))
        ) : (
          <Typography className={classes.noAuth}>
            No credentials added.
          </Typography>
        )}

        <div>
          <Button color="primary" onClick={onAdd}>
            Add credential
          </Button>
        </div>
      </ExpansionPanelDetails>
    </ExpansionPanel>
  );
}
const Authenticators = withStyles(styles)(AuthenticatorsComp);

export default Authenticators;

export const SecurityKeys = props => (
  <Authenticators
    {...props}
    title={
      <Typography gutterBottom={false} variant="body1">
        Credentials
      </Typography>
    }
  />
);

/* eslint-enable camelcase */
