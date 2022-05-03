import React from 'react';
import clsx from 'clsx';

import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Hidden from '@material-ui/core/Hidden';
import IconButton from '@material-ui/core/IconButton';
import Close from '@material-ui/icons/Close';

const styles = theme => ({
  root: {
    [theme.breakpoints.down('sm')]: {
      height: '100%',
      width: '100%',
      boxShadow: 'unset',
      borderRadius: 'unset',
    },
    [theme.breakpoints.up('md')]: {
      justifyContent: 'center',
      width: 558,
      minHeight: 563,
      border: 'solid 1px #aaaaaa',
    },
    display: 'flex',
    flexDirection: 'column',
  },
  notice: {
    position: 'absolute',
    right: 0,
    top: 0,
  },
  header: {
    backgroundColor: theme.palette.primary.main,
    color: theme.palette.primary.contrastText,
    width: '100%',
    height: 135,
    position: 'relative',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    flex: '0 1 auto',
  },
  headerWithAppBar: {
    [theme.breakpoints.down('sm')]: {
      height: 75,
      alignItems: 'unset',
    },
  },
  cardContentRoot: {
    position: 'relative',
    paddingTop: theme.spacing(6),
    paddingLeft: theme.spacing(10),
    paddingRight: theme.spacing(10),
    paddingBottom: theme.spacing(5),
    flex: '1 1 auto',
    [theme.breakpoints.down('sm')]: {
      paddingTop: theme.spacing(3),
      paddingLeft: theme.spacing(4),
      paddingRight: theme.spacing(4),
      paddingBottom: theme.spacing(1),
      flex: '1 1 0px',
    },
  },
  closeIcon: {
    position: 'absolute',
    top: 0,
    right: 0,
    color: theme.palette.primary.contrastText,
  },
  shrinkableTitle: {
    [theme.breakpoints.down('xs')]: {
      fontSize: '2rem',
    },
  },
});

export const GenericCardHeader = withStyles(styles)(
  ({
    classes,
    text,
    leftElement,
    rightElement,
    close = null,
    fullscreen = false,
  }) => (
    <div
      className={clsx(classes.header, {
        [classes.headerWithAppBar]: !fullscreen,
      })}
    >
      {leftElement}
      <Typography
        className={classes.shrinkableTitle}
        variant="h3"
        color="inherit"
      >
        {text}
      </Typography>
      {rightElement}
      {close && (
        <Hidden mdUp>
          <IconButton className={classes.closeIcon} onClick={close}>
            <Close />
          </IconButton>
        </Hidden>
      )}
    </div>
  )
);

export const GenericCard = withStyles(styles)(({ classes, children }) => (
  <Card classes={{ root: classes.root }}>{children}</Card>
));

export const GenericCardContent = withStyles(styles)(
  ({ classes, children }) => (
    <CardContent classes={{ root: classes.cardContentRoot }}>
      {children}
    </CardContent>
  )
);
