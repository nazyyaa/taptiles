async function getLoggedInUsername() {
    const response = await fetch('/api/user/session', {
        credentials: 'include'
    });
    const text = await response.text();

    if (text.startsWith("User logged in:")) {
        return text.replace("User logged in: ", "");
    }

    return alert("User not logged in");
}

async function isLoggedIn() {
    const response = await fetch('/api/user/session', { credentials: 'include' });
    const text = await response.text();
    return text.startsWith("User logged in:");
}

async function logout() {
    const response = await fetch('/api/user/logout', { method: 'POST', credentials: 'include' });
    if (response.ok) {
        window.location.reload();
    } else {
        alert("Logout failed.");
    }
}

async function hashPassword(password) {
    const encoder = new TextEncoder();
    const data = encoder.encode(password);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
    return hashHex;
}

function showNotification(title, message) {
    const notification = document.getElementById('notification');
    const titleElement = document.getElementById('notification-title');
    const messageElement = document.getElementById('notification-message');

    titleElement.textContent = title;
    messageElement.textContent = message;

    notification.classList.add('show');

    setTimeout(function() {
        notification.classList.remove('show');
    }, 2000);
}

function hideNotification() {
    const notification = document.getElementById('notification');
    notification.classList.remove('show');
}