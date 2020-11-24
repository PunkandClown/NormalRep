create
    definer = root@localhost procedure userNicknamePassEx(IN nickName1 varchar(45), IN passWord1 varchar(45),
                                                          OUT ex tinyint(1))
begin
    set ex = exists(select Nickname from users where Nickname=Nickname1 and Password=passWord1);
end;

