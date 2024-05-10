  drop table if exists users, mpa_ratings, films_users, friends, genres, films_genres, genres, films,
      directors, film_director, reviews, reviews_users_films,reviews_like cascade;

  create table if not exists mpa_ratings(
  id integer GENERATED BY DEFAULT AS IDENTITY primary key,
  name varchar
  );

  create table if not exists films(
  id integer GENERATED BY DEFAULT AS IDENTITY primary key,
  name varchar(255) not null,
  description varchar(200),
  release_date date,
  duration bigint,
  likes integer,
  mpa_id integer,
  foreign key (mpa_id) references mpa_ratings(id) on delete cascade
  );

  create table if not exists users(
  id integer GENERATED BY DEFAULT AS IDENTITY primary key,
  name varchar(100),
  email varchar(100) not null,
  login varchar(20) not null unique,
  birthday date
  );

  create table if not exists friends(
  user_id integer not null references users(id) on delete cascade,
  friend_id integer not null references users(id) on delete cascade,
  primary key (friend_id, user_id)
  );

  create table if not exists genres(
  id integer GENERATED BY DEFAULT AS IDENTITY primary key,
  name varchar
  );

  create table if not exists films_genres(
  film_id integer not null references films(id) on delete cascade,
  genre_id integer not null references genres(id) on delete cascade,
  primary key (film_id, genre_id)
  );

  create table if not exists films_users(
  film_id integer not null references films(id) on delete cascade,
  user_id integer not null references users(id) on delete cascade,
  primary key (film_id, user_id)
  );

  create table if not exists directors(
  id integer GENERATED BY DEFAULT AS IDENTITY primary key,
  name varchar(100)
  );

  CREATE table if not exists film_director (
    film_id integer not null references films(id) on delete cascade,
    director_id integer not null references directors(id) on delete cascade,
    PRIMARY KEY (film_id, director_id)
  );

  CREATE TABLE if not exists reviews (
    id integer GENERATED BY DEFAULT AS IDENTITY  PRIMARY KEY,
    content varchar(200),
    is_positive bool
  );

  CREATE TABLE if not exists reviews_users_films (
   review_id integer not null REFERENCES reviews(id) on delete cascade,
   user_id integer not null REFERENCES users(id) on delete cascade ,
   film_id integer not null REFERENCES films(id) on delete cascade
  );

  CREATE TABLE IF NOT EXISTS reviews_like(
  review_id integer NOT NULL ,
  user_id integer NOT NULL,
  is_like bool
  );

