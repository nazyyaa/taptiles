async function checkLoginStatus() {
    if (await isLoggedIn()) {
        document.getElementById("logout-link").style.display = "inline";
        document.getElementById("side-play-button").style.display = "block";
    } else {
        document.getElementById("logout-link").style.display = "none";
        document.getElementById("side-play-button").style.display = "none";
    }
}

window.onload = async function() {
    const loadingScreen = document.getElementById('loading-screen');
    await checkLoginStatus();
    setTimeout(function() {
        loadingScreen.classList.add('hidden');
    }, 1000);
};