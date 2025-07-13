drop index bookkeeper_user_username_key;

create unique index bookkeeper_user_username_key on bookkeeper_user (lower(bookkeeper_user.username));
