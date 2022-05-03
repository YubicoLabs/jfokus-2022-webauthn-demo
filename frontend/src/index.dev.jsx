// This empty Object.assign is needed to trigger the polyfill for it.
// It's here in the .dev file only because it's used by react-hot-loader
Object.assign({}, {}); // eslint-disable-line prefer-object-spread

// Create an unused promise to trigger the polyfill for it.
Promise.resolve(42);

import React from 'react';
import ReactDOM from 'react-dom';

import App from './components/App.dev';

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
