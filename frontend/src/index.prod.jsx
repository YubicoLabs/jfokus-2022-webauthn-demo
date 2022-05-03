import React from 'react';
import ReactDOM from 'react-dom';

import App from './components/App.prod';

import './styles/fonts.css';

function getRoot() {
  const root = document.getElementById('app');
  if (root) {
    return root;
  } else {
    const newRoot = document.createElement('div');
    newRoot.id = 'app';
    document.body.appendChild(newRoot);
    return newRoot;
  }
}

const root = getRoot();

ReactDOM.render(<App />, root);
