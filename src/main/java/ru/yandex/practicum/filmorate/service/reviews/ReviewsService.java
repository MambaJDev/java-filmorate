package ru.yandex.practicum.filmorate.service.reviews;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.reviews.ReviewsDao;
import ru.yandex.practicum.filmorate.dao.reviews.ReviewsLikeDao;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewsService {

    private final ReviewsDao reviewsDao;
    private final ReviewsLikeDao reviewsLikeDao;

    public Review createReview(Review review) {
        return reviewsDao.createReview(review);
    }

    public Review updateReview(Review review) {
        return reviewsDao.updateReview(review);
    }

    public List<Review> getReviewsByFilmId(Integer filmId, Integer count) {
        if (filmId == null) {
            return reviewsDao.getAllReviews();
        } else {
            return reviewsDao.getReviewsByFilmId(filmId, count);
        }
    }

    public Review getReviewById(Integer id) {
        return reviewsDao.getReviewById(id);
    }

    public void deleteAllReviews() {
        reviewsDao.deleteAllReviews();
    }

    public void deleteReviewById(Integer id) {
        reviewsDao.deleteReviewById(id);
    }

    public void addLike(Integer reviewId, Integer userId) {
        reviewsLikeDao.addLike(reviewId, userId);
    }

    public void addDislike(Integer reviewId, Integer userId) {
        reviewsLikeDao.addDislike(reviewId, userId);
    }

    public void deleteLike(Integer reviewId, Integer userId) {
        reviewsLikeDao.deleteLike(reviewId, userId);
    }

    public void deleteDislike(Integer reviewId, Integer userId) {
        reviewsLikeDao.deleteDislike(reviewId, userId);
    }
}
