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

export function loginPasswordless() {
  return handle(apiUrl('authenticate-passwordless'), 'POST');
}

export function logout() {
  return handle(apiUrl('logout'), 'POST');
}

export function getSession() {
  return handle(apiUrl('session'), 'GET');
}

export function removeAuthenticator(id) {
  return handle(apiUrl(`credential/${id}`), 'DELETE');
}

export function register(passwordless) {
  const formData = new URLSearchParams();
  formData.append('passwordless', passwordless || false);
  return handle(apiUrl('register'), 'POST', formData);
}

export function registerFinish(request, pkc, nickname) {
  return handle(request.actions.finish, 'POST', {
    requestId: request.request.requestId,
    credential: pkc,
    nickname,
  });
}

export function verifyFinish(request, pkc) {
  return handle(request.actions.finish, 'POST', {
    requestId: request.request.requestId,
    credential: pkc,
  });
}
