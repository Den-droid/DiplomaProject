insert into fields(name, "key", rule, type, scientometric_system_id, rule_type_id)
values ('Ім''я профілю', 'property', 'meta[property="og:title"]', 'STRING', 1, 2),
       ('Приналежність профілю КПІ', '', 'a[href="/citations?view_op=view_org&hl=uk&org=5596117057032671997"]',
        'BOOLEAN', 1, 1),
       ('Підтверджена електронна адреса', '', '#gsc_prf_ivh', 'BOOLEAN', 1, 1),
       ('Домашня сторінка', 'href', '#gsc_prf_ivh a', 'STRING', 1, 2),
       ('Ключові слова', '', '#gsc_prf_int a', 'STRING', 1, 3),
       ('Цитування', '1', '.gsc_rsb_std', 'INTEGER', 1, 5),
       ('h-індекс', '2', '.gsc_rsb_std', 'INTEGER', 1, 5),
       ('h-індекс з 2019', '3', '.gsc_rsb_std', 'INTEGER', 1, 5),
       ('Рік останньої публікації', '', 'span[class="gsc_a_h gsc_a_hc gs_ibl"]', 'INTEGER', 1, 1),
       ('Цитованість по рокам', '', '.gsc_g_al', 'STRING', 1, 4);