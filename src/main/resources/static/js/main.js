function showView(id) {
    document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
    document.getElementById(id).classList.add('active');
}

async function showRegisterMotd() {
    if (await isLoggedIn()) {
        valid_responce("Good luck " + await getLoggedInUsername() + "!");
    } else {
        showView('form-register-content');
    }
}

async function showLoginMotd() {
    if (await isLoggedIn()) {
        valid_responce("Good luck " + await getLoggedInUsername() + "!");
    } else {
        showView('form-login-content');
    }
}

async function register() {
    const username = document.getElementById('username_register').value;
    const password = document.getElementById('password_register').value;

    if (!username || !password || username.length < 3 || password.length < 3) {
        showNotification("Whoops...", "You must fill all the fields");
        return;
    }

    const crypted_password = await hashPassword(password);

    const response = await fetch('/api/user/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: crypted_password,

        })
    });

    if (response.ok) {
        const response = await fetch('/api/user/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify({
                username: username,
                password: crypted_password
            })
        });
        valid_responce("Good luck " + await getLoggedInUsername() + "!");
    } else {
        showNotification("Whoops...", "Username already taken");
    }
}

async function login() {
    const username = document.getElementById('username_login').value;
    const password = document.getElementById('password_login').value;

    if (!username || !password) {
        showNotification("Whoops...", "You must fill all the fields");
        return;
    }
    const crypted_password = await hashPassword(password);


    const response = await fetch('/api/user/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({
            username: username,
            password: crypted_password
        })
    });

    const message = await response.text();

    if (response.ok) {
        valid_responce("Good luck " + await getLoggedInUsername() + "!");
    } else {
        showNotification("Whoops...", "Incorrect login or password");
    }
}

function valid_responce(message) {
    if (!message) message = "Good luck!";

    document.getElementById('username-message').innerText = message;
    showView('success-message');
    setTimeout(function () {
        window.location.href = '/taptiles/play';
    }, 2000);
}

async function checkLoginStatus() {
    if (await isLoggedIn()) {
        document.getElementById("logout-link").style.display = "inline";
    } else {
        document.getElementById("logout-link").style.display = "none";
    }
}

window.onload = async function() {
    const loadingScreen = document.getElementById('loading-screen');
    await checkLoginStatus();
    setTimeout(function() {
        loadingScreen.classList.add('hidden');
    }, 1000);
};
