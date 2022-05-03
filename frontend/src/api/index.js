import { requestHandleResponse as handle, apiUrlBuilder } from './util';

const apiUrl = apiUrlBuilder('/api/v1');

export function createUser(username, password) {
  const formData = new URLSearchParams();
  formData.append('username', username);
  formData.append('password', password);
  return handle(apiUrl('create-user'), 'POST', formData);
}

export function login(username, password) {
  const formData = new URLSearchParams();
  formData.append('username', username);
  formData.append('password', password);
  return handle(apiUrl('authenticate'), 'POST', formData);
}

export function logout() {
  return handle(apiUrl('logout'), 'POST');
}

export function getSession() {
  return handle(apiUrl('session'), 'GET');
}
