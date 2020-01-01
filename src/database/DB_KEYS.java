package database;

/**
 * Created by Michael on 4/18/2017.
 */
public class DB_KEYS {
    public static final String TABLE_MENUS = "menus";
    public static final String COLUMN_MENUS_ID = "id";
    public static final String COLUMN_MENUS_NAME = "name";
    public static final String COLUMN_MENUS_CATEGORY_ID = "category_id";
    public static final String COLUMN_MENUS_PRICE = "price";
    public static final String COLUMN_MENUS_IMAGE_URL = "image_url";

    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ORDERS_ID = "id";
    public static final String COLUMN_ORDERS_CUSTOMER_CODE = "customer_code";
    public static final String COLUMN_ORDERS_DATE = "date";
    public static final String COLUMN_ORDERS_STATUS_ID = "status_id";
    public static final String COLUMN_ORDERS_PAYMENT_METHOD_ID = "payment_method_id";
    public static final String COLUMN_ORDERS_PROMO_AMOUNT = "promo_amount";
    public static final String COLUMN_ORDERS_DELETED_AT = "deleted_at";
    public static final String COLUMN_ORDERS_NOTES = "notes";

    public static final String TABLE_ORDER_DETAILS = "order_details";
    public static final String COLUMN_ORDER_DETAILS_ID = "id";
    public static final String COLUMN_ORDER_DETAILS_ORDER_ID= "order_id";
    public static final String COLUMN_ORDER_DETAILS_MENU_ID = "menu_id";
    public static final String COLUMN_ORDER_DETAILS_QTY = "qty";
    public static final String COLUMN_ORDER_DETAILS_PRICE = "price";
    public static final String COLUMN_ORDER_DETAILS_FOOD_STATUS_ID = "food_status_id";
    public static final String COLUMN_ORDER_DETAILS_NOTES = "notes";

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USERS_ID = "id";
    public static final String COLUMN_USERS_NAME = "name";
    public static final String COLUMN_USERS_PASSWORD = "password";
    public static final String COLUMN_USERS_SECRET_CODE = "secret_code";

    public static final String TABLE_STATUSES = "statuses";
    public static final String COLUMN_STATUSES_ID = "id";
    public static final String COLUMN_STATUSES_SLUG = "slug";
    public static final String COLUMN_STATUSES_NAME = "name";

    public static final String TABLE_PAYMENT_METHODS = "payment_methods";
    public static final String COLUMN_PAYMENT_METHODS_ID = "id";
    public static final String COLUMN_PAYMENT_METHODS_SLUG = "slug";
    public static final String COLUMN_PAYMENT_METHODS_NAME = "name";

    public static final String TABLE_FOOD_STATUSES = "food_statuses";
    public static final String COLUMN_FOOD_STATUSES_ID = "id";
    public static final String COLUMN_FOOD_STATUSES_SLUG = "slug";
    public static final String COLUMN_FOOD_STATUSES_NAME = "name";

    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CATEGORIES_ID = "id";
    public static final String COLUMN_CATEGORIES_SLUG = "slug";
    public static final String COLUMN_CATEGORIES_NAME = "name";
}
