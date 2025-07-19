async function checkLoginStatus() {
    if (await isLoggedIn()) {
        const username = await getLoggedInUsername();
        const userlink = document.getElementById('user-link');

        userlink.textContent = username;
        await updatePlayerScore();
    } else {
        window.location.href = '/taptiles';
    }
}

function changeMainWindow(id) {
    const userMenu = document.getElementById("user-menu");

    userMenu.classList.remove("visible");
    const playBox = document.getElementById("play-box");
    const setLevel = document.getElementById("container-settings-level");
    const setNickname = document.getElementById("container-settings-nickname");
    const setPassword = document.getElementById("container-settings-password");

    playBox.classList.add("hide");

    switch (id) {
        case 1:
            setLevel.classList.toggle("visible");


            setNickname.classList.remove("visible");
            setPassword.classList.remove("visible");
            break;
        case 2:
            setNickname.classList.toggle("visible");

            setLevel.classList.remove("visible");
            setPassword.classList.remove("visible");
            break;
        case 3:
            setPassword.classList.toggle("visible");

            setLevel.classList.remove("visible");
            setNickname.classList.remove("visible");
            break;
        default:
            break
    }
}

function selectLevel(level) {
    const setLevel = document.getElementById("container-settings-level");
    const playBox = document.getElementById("play-box");
    setLevel.classList.remove("visible");
    stopTimer();

    fetch('/taptiles/play/level?value=' + level)
        .then(response => response.text())
        .then(html => {
            document.getElementById('board-box').innerHTML = html;
        })

    showNotification("Success!", "Enjoy the game!");
    playBox.classList.remove("hide");
}

async function changePlayerInfo(id) {
    switch (id) {
        case 1: {
            const usernameInput = document.getElementById('new_username');
            const username = usernameInput.value.trim();
            if (username.length < 3) {
                showNotification("Whoops...", "Enter a valid nickname (at least 3 characters)");
                return;
            }

            const response = await fetch('/api/user/change-username', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({ newUsername: username })
            });

            if (response.ok) {
                showNotification("Success!", "Your nickname has been changed");
                document.getElementById("container-settings-nickname").classList.remove("visible");
                setTimeout(() => window.location.reload(), 2000);
            } else {
                const message = await response.text();
                showNotification("Whoops...", message || "Failed to change nickname");
            }
            break;
        }

        case 2: {
            const passwordInput = document.getElementById('new_password');
            const password = passwordInput.value;
            if (password.length < 3) {
                showNotification("Whoops...", "Enter a valid password (at least 3 characters)");
                return;
            }

            const cryptedPassword = await hashPassword(password);

            const response = await fetch('/api/user/change-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({ newPassword: cryptedPassword })
            });

            if (response.ok) {
                showNotification("Success!", "Your password has been changed");
                document.getElementById("container-settings-password").classList.remove("visible");
                setTimeout(() => window.location.reload(), 2000);
            } else {
                const message = await response.text();
                showNotification("Whoops...", message || "Failed to change password");
            }
            break;
        }

        default:
            break;
    }
}

function reset() {
    stopTimer();
    const playBox = document.getElementById("play-box");
    playBox.classList.add("hide");
    setTimeout(function() {
        showNotification("Success!", "Board has been reset");
        fetch('/taptiles/play/reset')
            .then(response => response.text())
            .then(html => {
                document.getElementById('board-box').innerHTML = html;
            });

        playBox.classList.remove("hide");
    }, 300);
}

function undo() {
    const playBox = document.getElementById('play-box');
    fetch('/taptiles/play/undo')
        .then(response => {
            if (!response.ok) {
                return '';
            }
            return response.text();
        })
        .then(html => {
            if (html) {
                playBox.classList.add('hide');
                setTimeout(function() {
                    document.getElementById('board-box').innerHTML = html;
                    showNotification("Success!", "The last movement was canceled");
                    updatePlayerScore();
                    playBox.classList.remove('hide');
                }, 300);
            }
        });
}


function setMenu() {
    const userLink = document.getElementById("user-link");
    const userMenu = document.getElementById("user-menu");

    const rect = userLink.getBoundingClientRect();
    const linkWidth = rect.width;
    const menuWidth = userMenu.offsetWidth;

    const left = rect.left + window.scrollX + (linkWidth / 2) - (menuWidth / 2);
    const top = rect.bottom + window.scrollY + 5;

    userMenu.style.left = `${left}px`;
    userMenu.style.top = `${top}px`;

    userMenu.classList.toggle("visible");
}
function choose(row, col) {
    fetch(`/taptiles/play/choose?row=${row}&col=${col}`)
        .then(response => response.text())
        .then(html => {
            if (html.includes("Game Won!")) {
                showNotification("Game Over", html.includes("Game Won!") ? "Ви виграли 🎉" : "Dead-end – гра завершена");
                endGame(1);
            } else if (html.includes("Game Lost!")) {
                endGame(2);
            } else {
                document.getElementById('board-box').innerHTML = html;
                startTimer();
            }
        })
        .catch(err => {
            console.error("Помилка при виборі плитки:", err);
        });
}

function endGame(id) {
    updatePlayerScore();
    stopTimer();
    const playBox = document.getElementById("play-box");
    playBox.classList.add("hide");

    switch (id) {
        case 1:
            showNotification("Success!", "Game Over! You won!");
            break;
        case 2:
            showNotification("Whoops...", "Game Over! You lost! Try again!");
            break;
        default:
            break;
    }
    setTimeout(function() {
        fetch('/taptiles/play/html')
            .then(response => response.text())
            .then(html => {
                document.getElementById('board-box').innerHTML = html;
            })
        playBox.classList.remove("hide");
    }, 2000);
}

let timer = false;
let seconds = 0;
let timerInterval = null;

function startTimer() {
    if (timer === true) {
        return;
    }
    timer = true;
    timerInterval = setInterval(() => {
        seconds++;
        const mins = String(Math.floor(seconds / 60)).padStart(2, '0');
        const secs = String(seconds % 60).padStart(2, '0');
        document.getElementById("timer").textContent = `Time: ${mins}:${secs}`;
    }, 1000);
}

function stopTimer() {
    clearInterval(timerInterval);
    timer = false;
    seconds = 0;
    timerInterval = 0;
}

async function updatePlayerScore() {
    try {
        const res = await fetch('/taptiles/play/getCurrentScore', { credentials: 'include' });
        if (!res.ok) throw new Error('Score request failed');

        const score = await res.text();
        const scoreElement = document.getElementById('player-score');
        if (scoreElement) {
            scoreElement.textContent = `Score: ${score}`;
        } else {
            console.error('Element with id "score" not found');
        }
    } catch (e) {
        console.error('Failed to fetch score:', e);
    }
}

document.addEventListener("mousedown", function (event) {
    const userMenu = document.getElementById("user-menu");
    const userLink = document.getElementById("user-link");
    const setLevel = document.getElementById("container-settings-level");
    const setNickname = document.getElementById("container-settings-nickname");
    const setPassword = document.getElementById("container-settings-password");
    const playBox = document.getElementById("play-box");

    if (!userMenu.contains(event.target) && !userLink.contains(event.target)) {
        userMenu.classList.remove("visible");
    }

    let somethingClosed = false;

    if (setLevel.classList.contains("visible") && !setLevel.contains(event.target) && event.target !== userLink) {
        setLevel.classList.remove("visible");
        somethingClosed = true;
    }

    if (setNickname.classList.contains("visible") && !setNickname.contains(event.target) && event.target !== userLink) {
        setNickname.classList.remove("visible");
        somethingClosed = true;
    }

    if (setPassword.classList.contains("visible") && !setPassword.contains(event.target) && event.target !== userLink) {
        setPassword.classList.remove("visible");
        somethingClosed = true;
    }

    setTimeout(function() {
        const noneVisible =
            !setLevel.classList.contains("visible") &&
            !setNickname.classList.contains("visible") &&
            !setPassword.classList.contains("visible");

        if (somethingClosed && noneVisible) {
            playBox.classList.remove("hide");
        }
    }, 100);
});

window.onload = async function() {
    const loadingScreen = document.getElementById('loading-screen');
    await checkLoginStatus();
    setTimeout(function() {
        loadingScreen.classList.add('hidden');
    }, 1000);
};