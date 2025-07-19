async function checkLoginStatus() {
    if (await isLoggedIn()) {
        document.getElementById("logout-link").style.display = "inline";
        document.getElementById("comment-form-container").className = "d-flex justify-content-center";
        document.getElementById("side-play-button").style.display = "block";
    } else {
        document.getElementById("logout-link").style.display = "none";
        document.getElementById("comment-form-container").style.display = "none";
        document.getElementById("side-play-button").style.display = "none";
    }
}

async function submit_comment() {
    const comment = document.getElementById("comment").value;
    if (comment == null || comment === "") {
        showNotification("", "Please provide a comment.");
        return;
    }

    const username = await getLoggedInUsername();

    const response = await fetch("/api/comment", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            game: "taptiles",
            player: username,
            comment: comment,
            commentedOn: new Date()
        })
    });

    if (response.ok) {
        showNotification("", "Your comment submitted successfully!");
        setTimeout(() => window.location.reload(), 2000);
    } else {
        showNotification("", "Failed to submit comment. Please try again.");
    }
}

window.onload = async function() {
    const loadingScreen = document.getElementById('loading-screen');
    await checkLoginStatus();
    setTimeout(function() {
        loadingScreen.classList.add('hidden');
    }, 1000);
};