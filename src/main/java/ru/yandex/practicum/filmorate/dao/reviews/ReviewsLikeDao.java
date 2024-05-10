package ru.yandex.practicum.filmorate.dao.reviews;


public interface ReviewsLikeDao {

    void addLike(Integer reviewId, Integer userId);

    void addDislike(Integer reviewId, Integer userId);

    void deleteLike(Integer reviewId, Integer userId);

    void deleteDislike(Integer reviewId, Integer userId);
}
