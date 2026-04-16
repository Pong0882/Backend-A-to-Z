CREATE TABLE `users` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT,
    `email`         VARCHAR(100)    NOT NULL,
    `password`      VARCHAR(255)    NULL,
    `nickname`      VARCHAR(50)     NOT NULL,
    `profile_image` VARCHAR(500)    NULL,
    `point_balance` BIGINT          NOT NULL DEFAULT 0,
    `role`          VARCHAR(20)     NOT NULL DEFAULT 'ROLE_USER',
    `login_type`    VARCHAR(10)     NOT NULL DEFAULT 'LOCAL',
    `is_active`     BOOLEAN         NOT NULL DEFAULT TRUE,
    `deleted_at`    DATETIME        NULL,
    `created_at`    DATETIME        NOT NULL,
    `updated_at`    DATETIME        NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_email` (`email`),
    UNIQUE KEY `uk_users_nickname` (`nickname`)
);

CREATE TABLE `oauth_accounts` (
    `id`          BIGINT          NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT          NOT NULL,
    `provider`    VARCHAR(20)     NOT NULL,
    `provider_id` VARCHAR(100)    NOT NULL,
    `created_at`  DATETIME        NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_oauth_provider_id` (`provider`, `provider_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

CREATE TABLE `broker_accounts` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT,
    `user_id`           BIGINT          NOT NULL,
    `broker`            VARCHAR(20)     NOT NULL,
    `account_type`      VARCHAR(10)     NOT NULL,
    `appkey`            VARCHAR(100)    NOT NULL,
    `appsecret`         VARCHAR(200)    NOT NULL,
    `balance`           DECIMAL(15, 2)  NOT NULL DEFAULT 0,
    `balance_synced_at` DATETIME        NULL,
    `is_active`         BOOLEAN         NOT NULL DEFAULT TRUE,
    `created_at`        DATETIME        NOT NULL,
    `updated_at`        DATETIME        NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_broker_accounts` (`user_id`, `broker`, `account_type`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

CREATE TABLE `stocks` (
    `id`     BIGINT       NOT NULL AUTO_INCREMENT,
    `code`   VARCHAR(20)  NOT NULL,
    `name`   VARCHAR(50)  NOT NULL,
    `market` VARCHAR(10)  NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_stocks_code_market` (`code`, `market`)
);

CREATE TABLE `stock_candles` (
    `id`          BIGINT          NOT NULL AUTO_INCREMENT,
    `stock_id`    BIGINT          NOT NULL,
    `interval`    VARCHAR(10)     NOT NULL,
    `trade_time`  DATETIME        NOT NULL,
    `open_price`  DECIMAL(12, 4)  NOT NULL,
    `high_price`  DECIMAL(12, 4)  NOT NULL,
    `low_price`   DECIMAL(12, 4)  NOT NULL,
    `close_price` DECIMAL(12, 4)  NOT NULL,
    `volume`      BIGINT          NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_stock_candles` (`stock_id`, `interval`, `trade_time`),
    FOREIGN KEY (`stock_id`) REFERENCES `stocks` (`id`)
);

CREATE TABLE `stock_prices` (
    `id`          BIGINT          NOT NULL AUTO_INCREMENT,
    `stock_id`    BIGINT          NOT NULL,
    `trade_date`  DATE            NOT NULL,
    `open_price`  DECIMAL(12, 4)  NOT NULL,
    `high_price`  DECIMAL(12, 4)  NOT NULL,
    `low_price`   DECIMAL(12, 4)  NOT NULL,
    `close_price` DECIMAL(12, 4)  NOT NULL,
    `volume`      BIGINT          NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_stock_prices` (`stock_id`, `trade_date`),
    FOREIGN KEY (`stock_id`) REFERENCES `stocks` (`id`)
);

CREATE TABLE `watchlists` (
    `id`          BIGINT          NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT          NOT NULL,
    `stock_id`    BIGINT          NOT NULL,
    `alert_price` DECIMAL(12, 4)  NULL,
    `created_at`  DATETIME        NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_watchlists` (`user_id`, `stock_id`),
    FOREIGN KEY (`user_id`)  REFERENCES `users` (`id`),
    FOREIGN KEY (`stock_id`) REFERENCES `stocks` (`id`)
);

CREATE TABLE `strategies` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`            BIGINT       NOT NULL,
    `broker_account_id`  BIGINT       NOT NULL,
    `stock_id`           BIGINT       NOT NULL,
    `name`               VARCHAR(100) NOT NULL,
    `status`             VARCHAR(10)  NOT NULL DEFAULT 'INACTIVE',
    `order_quantity`     INT          NOT NULL,
    `last_checked_at`    DATETIME     NULL,
    `created_at`         DATETIME     NOT NULL,
    `updated_at`         DATETIME     NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`)           REFERENCES `users` (`id`),
    FOREIGN KEY (`broker_account_id`) REFERENCES `broker_accounts` (`id`),
    FOREIGN KEY (`stock_id`)          REFERENCES `stocks` (`id`)
);

CREATE TABLE `strategy_conditions` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `strategy_id` BIGINT       NOT NULL,
    `type`        VARCHAR(10)  NOT NULL,
    `indicator`   VARCHAR(50)  NOT NULL,
    `params`      JSON         NOT NULL,
    `created_at`  DATETIME     NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`strategy_id`) REFERENCES `strategies` (`id`)
);

CREATE TABLE `orders` (
    `id`                BIGINT          NOT NULL AUTO_INCREMENT,
    `user_id`           BIGINT          NOT NULL,
    `strategy_id`       BIGINT          NULL,
    `broker_account_id` BIGINT          NOT NULL,
    `stock_id`          BIGINT          NOT NULL,
    `order_type`        VARCHAR(10)     NOT NULL,
    `price_type`        VARCHAR(10)     NOT NULL,
    `status`            VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    `quantity`          INT             NOT NULL,
    `price`             DECIMAL(12, 4)  NULL,
    `filled_quantity`   INT             NOT NULL DEFAULT 0,
    `created_at`        DATETIME        NOT NULL,
    `updated_at`        DATETIME        NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`)           REFERENCES `users` (`id`),
    FOREIGN KEY (`strategy_id`)       REFERENCES `strategies` (`id`),
    FOREIGN KEY (`broker_account_id`) REFERENCES `broker_accounts` (`id`),
    FOREIGN KEY (`stock_id`)          REFERENCES `stocks` (`id`)
);

CREATE TABLE `order_executions` (
    `id`          BIGINT          NOT NULL AUTO_INCREMENT,
    `order_id`    BIGINT          NOT NULL,
    `quantity`    INT             NOT NULL,
    `price`       DECIMAL(12, 4)  NOT NULL,
    `executed_at` DATETIME        NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
);

CREATE TABLE `portfolios` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT   NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_portfolios_user_id` (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

CREATE TABLE `holdings` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT,
    `portfolio_id`  BIGINT          NOT NULL,
    `stock_id`      BIGINT          NOT NULL,
    `quantity`      INT             NOT NULL,
    `average_price` DECIMAL(12, 4)  NOT NULL,
    `is_hidden`     BOOLEAN         NOT NULL DEFAULT FALSE,
    `created_at`    DATETIME        NOT NULL,
    `updated_at`    DATETIME        NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_holdings` (`portfolio_id`, `stock_id`),
    FOREIGN KEY (`portfolio_id`) REFERENCES `portfolios` (`id`),
    FOREIGN KEY (`stock_id`)     REFERENCES `stocks` (`id`)
);
