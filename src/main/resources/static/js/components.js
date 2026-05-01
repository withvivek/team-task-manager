function renderSidebar(activePage) {
    const userData = localStorage.getItem('user');
    if (!userData) return;
    const user = JSON.parse(userData);
    const isAdmin = user.roles && user.roles.includes('ROLE_ADMIN');

    const sidebar = `
        <div class="sidebar">
            <h2>🚀 TaskManager</h2>
            <nav>
                <a href="dashboard.html" class="${activePage === 'dashboard' ? 'active' : ''}">Dashboard</a>
                <a href="projects.html" class="${activePage === 'projects' ? 'active' : ''}">Projects</a>
                <a href="tasks.html" class="${activePage === 'tasks' ? 'active' : ''}">Tasks</a>
            </nav>
            <div style="margin-bottom: 1rem; font-size: 0.8rem; color: #94a3b8;">
                Logged in as: <strong>${user.username}</strong><br>
                Role: ${isAdmin ? 'Admin' : 'Member'}
            </div>
            <button onclick="logout()" class="logout-btn">Logout</button>
        </div>
    `;
    document.body.insertAdjacentHTML('afterbegin', sidebar);
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'login.html';
}

function checkAuth() {
    if (!localStorage.getItem('token')) {
        window.location.href = 'login.html';
    }
}
