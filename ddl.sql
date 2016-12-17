# DDL
create database javaedu;

create table frame(
  id          integer(10),
  frame_no    integer(10),
  score       integer(10),
  entry_date  datetime,
  upd_date    datetime,
  version     integer(10),
  PRIMARY KEY(id, frame_no)
);

create table pin (
  frame_id    integer(10),
  frame_no    integer(10),
  throw       integer(10),
  count       integer(10),
  entry_date  datetime,
  upd_date    datetime,
  version     integer,
  PRIMARY KEY(frame_id, frame_no, throw)
);
