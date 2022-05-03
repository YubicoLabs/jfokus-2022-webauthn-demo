export function request(url, method, body = null) {
  const opts = {
    method,
    credentials: 'same-origin',
  };

  if (body) {
    if (!(body instanceof URLSearchParams)) {
      opts.headers = { 'Content-Type': 'application/json' };
    }
  }

  if (body && method !== 'GET' && method !== 'HEAD' && method !== 'DELETE') {
    if (body instanceof URLSearchParams) {
      opts.body = body;
    } else {
      opts.body = JSON.stringify(body);
    }
  }

  return fetch(url, opts)
    .then(response => response.json())
    .catch(res => ({ success: false, message: 'Something went wrong' }));
}

export function handleServerResponse(res) {
  if (res.success) {
    return res;
  } else {
    throw new Error('The response from the server does not conform');
  }
}

export function apiUrlBuilder(base) {
  return path => `${base}/${path}`.replace(/\/+/gu, '/');
}

export function requestHandleResponse(path, method, body) {
  return request(path, method, body).then(handleServerResponse);
}
