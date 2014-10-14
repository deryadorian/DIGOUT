insert into USERS(fullname, email, password, username, role, is_system_user, origin, is_following_notification, is_friends_notification, is_shortlists_notification)
values('Guest','guest@digout.com','871b4aa6457f2a1ed40f42bd4554c2e6','digout_guest', 1, 1, 0, 0, 0, 0)
;

insert into APPLICATION_VERSIONS(client_platform_type, client_platform_version, server_platform_version, downloadUrl)
values('IOS', '5.1', '1.0', 'some.com')

/*test data for bank*/
insert into BANK_INFO(cardholder_present_code, installmentCnt, merchant_ID, mode, moto_ind, name, original_retref_num, prov_user_ID,
terminal_ID, transaction_type, user_ID, version, password, bank_uri) values ('0','', '7000679', 'TEST', 'N', 'test', '', 'PROVAUT',
'30691297', 'sales', 'GARANTI', 'v0.01', '123qweASD', 'https://sanalposprovtest.garanti.com.tr/VPServlet')
;