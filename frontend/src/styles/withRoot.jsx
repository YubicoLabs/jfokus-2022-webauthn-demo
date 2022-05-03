import React, { useEffect } from 'react';
import { createMuiTheme } from '@material-ui/core/styles';
import {
  withStyles,
  createGenerateClassName,
  ThemeProvider,
} from '@material-ui/styles';
import CssBaseline from '@material-ui/core/CssBaseline';

// A theme with custom primary and secondary color.
export const defaultThemeSpec = {
  palette: {
    primary: {
      main: '#9aca3c',
      contrastText: '#ffffff',
    },
    secondary: { main: '#325f74' },
    text: {
      primary: '#666',
      secondary: '#9c9c9c',
    },
  },
  typography: {
    useNextVariants: true,
    fontFamily: [
      'HelveticaNeue',
      '"Helvetica Neue"',
      '"Open Sans"',
      'Helvetica',
      'Arial',
      'sans-serif',
    ],
  },
};

const styles = {
  '@global': {
    html: { height: '100%' },
    body: { height: '100%', backgroundColor: '#f1f1f1' },
    '#app': { height: '100%' },
    a: { color: 'inherit', visited: 'unset' },
  },
};

const generateClassName = createGenerateClassName({ productionPrefix: 'p' });

const MetaThemeColor = ({ color }) => {
  useEffect(() => {
    const node = document.querySelector('meta[name="theme-color"]');

    if (node) {
      if (color !== node.content) {
        node.setAttribute('content', color);
      }
    } else {
      const meta = document.createElement('meta');
      meta.name = 'theme-color';
      meta.content = color;
      document.getElementsByTagName('head')[0].appendChild(meta);
    }
  }, [color]);

  return <React.Fragment />;
};

export default function withRoot(Component) {
  const WithRoot = ({ isNamespace, isDone, failed, generalConf, ...props }) => {
    let theme = defaultThemeSpec;
    if (isNamespace && isDone && !failed) {
      const { theme: nsTheme = {} } = generalConf;
      theme = {
        ...theme,
        palette: { ...theme.palette, ...nsTheme.palette },
      };
    }

    return (
      <ThemeProvider theme={createMuiTheme(theme)}>
        <CssBaseline />
        {isDone && <MetaThemeColor color={theme.palette.primary.main} />}
        <Component {...props} />
      </ThemeProvider>
    );
  };

  return withStyles(styles)(WithRoot);
}
