const API_BASE_URL = '/api';

const apiFetch = async (endpoint, options = {}) => {
    const isAuthEndpoint = endpoint.includes('/auth/');
    const token = !isAuthEndpoint ? localStorage.getItem('token') : null;
    
    const headers = {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
        ...options.headers,
    };

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...options,
        headers,
    });

    if (response.status === 401 && !isAuthEndpoint) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        if (!window.location.pathname.endsWith('login.html')) {
            window.location.href = 'login.html';
            return;
        }
    }

    let data;
    try {
        data = await response.json();
    } catch (e) {
        data = { message: 'Network error or invalid server response' };
    }

    if (!response.ok) {
        throw new Error(data.message || 'Something went wrong');
    }
    return data;
};

const AuthAPI = {
    login: (username, password) => apiFetch('/auth/signin', {
        method: 'POST',
        body: JSON.stringify({ username, password })
    }),
    signup: (username, email, password, role) => apiFetch('/auth/signup', {
        method: 'POST',
        body: JSON.stringify({ username, email, password, role: [role] })
    })
};

const ProjectAPI = {
    getAll: () => apiFetch('/projects'),
    create: (data) => apiFetch('/projects', {
        method: 'POST',
        body: JSON.stringify(data)
    })
};

const TaskAPI = {
    getAll: () => apiFetch('/tasks'),
    create: (data) => apiFetch('/tasks', {
        method: 'POST',
        body: JSON.stringify(data)
    }),
    updateStatus: (id, status) => apiFetch(`/tasks/${id}/status`, {
        method: 'PUT',
        body: JSON.stringify({ status })
    }),
    delete: (id) => apiFetch(`/tasks/${id}`, {
        method: 'DELETE'
    })
};

const UserAPI = {
    getAll: () => apiFetch('/users')
};
