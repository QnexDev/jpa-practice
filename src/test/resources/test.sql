 select
        *
    from
        Library library0_
 left outer join Library_Author authors1_ on library0_.id=authors1_.Library_id
 left outer join Author author2_ on authors1_.authors_id=author2_.id
 left outer join Library_Book books3_ on library0_.id=books3_.library_id
 left outer join Book book4_ on books3_.book_id=book4_.id
 left outer join LibraryType librarytyp5_ on library0_.library_type_id=librarytyp5_.id
 where
        library0_.id=1

 select
        *
    from
        Library library0_
 left outer join Library_Author authors1_ on library0_.id=authors1_.Library_id
 left outer join Author author2_ on authors1_.authors_id=author2_.id
 left outer join Library_Book books3_ on library0_.id=books3_.library_id
 left outer join Book book4_ on books3_.book_id=book4_.id
 left outer join LibraryType librarytyp5_ on library0_.library_type_id=librarytyp5_.id
 where
        library0_.id=1

-- Set implementation
--0 = {LinkedCaseInsensitiveMap@6051}  size = 9
-- 0 = {LinkedHashMap$Entry@6057} "id" -> "666"
-- 1 = {LinkedHashMap$Entry@6058} "created" -> "null"
-- 2 = {LinkedHashMap$Entry@6059} "name" -> "Main"
-- 3 = {LinkedHashMap$Entry@6060} "version" -> "1"
-- 4 = {LinkedHashMap$Entry@6061} "library_type_id" -> "null"
-- 5 = {LinkedHashMap$Entry@6062} "library_id" -> "null"
-- 6 = {LinkedHashMap$Entry@6063} "authors_id" -> "null"
-- 7 = {LinkedHashMap$Entry@6064} "book_id" -> "1"
-- 8 = {LinkedHashMap$Entry@6065} "type" -> "null"


--result = {ArrayList@5621}  size = 1
-- 0 = {LinkedCaseInsensitiveMap@5623}  size = 10
--  0 = {LinkedHashMap$Entry@5629} "id" -> "666"
--  1 = {LinkedHashMap$Entry@5630} "created" -> "null"
--  2 = {LinkedHashMap$Entry@5631} "name" -> "Main"
--  3 = {LinkedHashMap$Entry@5632} "version" -> "1"
--  4 = {LinkedHashMap$Entry@5633} "library_type_id" -> "null"
--  5 = {LinkedHashMap$Entry@5634} "library_id" -> "null"
--  6 = {LinkedHashMap$Entry@5635} "authors_id" -> "null"
--  7 = {LinkedHashMap$Entry@5636} "authors_order" -> "null"
--  8 = {LinkedHashMap$Entry@5637} "book_id" -> "1"
--  9 = {LinkedHashMap$Entry@5638} "type" -> "null"


