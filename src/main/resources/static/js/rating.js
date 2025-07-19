document.addEventListener("DOMContentLoaded", function () {
    const stars = document.querySelectorAll('#rating-stars .star');
    const ratingInput = document.getElementById('rating-input');

    if (!stars.length || !ratingInput) return;

    let currentRating = 0;

    stars.forEach(star => {
        const val = parseInt(star.getAttribute('data-value'));

        star.addEventListener('click', () => {
            currentRating = val;
            ratingInput.value = val;
            updateStars();
        });

        star.addEventListener('mouseover', () => {
            updateStars(val);
        });

        star.addEventListener('mouseout', () => {
            updateStars();
        });
    });

    function updateStars(hoverValue = 0) {
        stars.forEach(star => {
            const val = parseInt(star.getAttribute('data-value'));
            star.classList.remove('selected', 'hovered');

            if (hoverValue > 0) {
                if (val <= hoverValue) star.classList.add('hovered');
            } else {
                if (val <= currentRating) star.classList.add('selected');
            }
        });
    }
});

async function checkLoginStatus() {
    if (await isLoggedIn()) {
        document.getElementById("logout-link").style.display = "inline";
        document.getElementById("rating-form-container").className = "d-flex justify-content-center";
        document.getElementById("side-play-button").style.display = "block";
    } else {
        document.getElementById("logout-link").style.display = "none";
        document.getElementById("rating-form-container").style.display = "none";
        document.getElementById("side-play-button").style.display = "none";
    }
}

async function submit_rating() {
    const rating = parseInt(document.getElementById("rating-input").value, 10); // Перетворюємо на ціле число
    if (isNaN(rating) || rating < 1 || rating > 5) {
        showNotification("", "Please provide a valid rating between 1 and 5.");
        return;
    }

    const username = await getLoggedInUsername();

    const response = await fetch('/api/rating', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            game: 'taptiles',
            player: username,
            rating: rating,
            ratedOn: new Date()
        })
    });

    if (response.ok) {
        showNotification("", "Your rating submitted successfully!");
        setTimeout(() => window.location.reload(), 2000);
    } else {
        showNotification("", "Failed to submit rating. Please try again.");
    }
}


window.onload = async function() {
    const loadingScreen = document.getElementById('loading-screen');
    await checkLoginStatus();
    setTimeout(function() {
        loadingScreen.classList.add('hidden');
    }, 1000);
};