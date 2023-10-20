package com.bahmet.dao;

import com.bahmet.exception.DatabaseException;
import com.bahmet.exception.DuplicateExchangeRateException;
import com.bahmet.model.ExchangeRate;
import com.bahmet.utils.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDAO {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();

    public List<ExchangeRate> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();

        String query = "SELECT * FROM exchange_rate";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                exchangeRates.add(new ExchangeRate(
                        resultSet.getLong("id"),
                        currencyDAO.getCurrencyById(resultSet.getLong("base_currency_id")),
                        currencyDAO.getCurrencyById(resultSet.getLong("target_currency_id")),
                        resultSet.getDouble("rate")
                ));
            }

            return exchangeRates;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public ExchangeRate getExchangeRateByCodes(String baseCode, String targetCode) {
        String query = "SELECT exchange_rate.* FROM exchange_rate JOIN currency base ON exchange_rate.base_currency_id = base.id JOIN currency target ON exchange_rate.target_currency_id = target.id WHERE base.code = ? AND target.code = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, baseCode);
            preparedStatement.setString(2, targetCode);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            return new ExchangeRate(
                    resultSet.getLong("id"),
                    currencyDAO.getCurrencyById(resultSet.getLong("base_currency_id")),
                    currencyDAO.getCurrencyById(resultSet.getLong("target_currency_id")),
                    resultSet.getDouble("rate")
            );
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public ExchangeRate addExchangeRate(ExchangeRate exchangeRate) {
        if (getExchangeRateByCodes(exchangeRate.getBaseCurrency().getCode(),
                exchangeRate.getTargetCurrency().getCode()) != null) {
            throw new DuplicateExchangeRateException();
        }


        String query = "INSERT INTO exchange_rate(base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setLong(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setDouble(3, exchangeRate.getRate());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating ExchangeRate failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Creating ExchangeRate failed, no ID obtained.");
                }

                ExchangeRate addedExchangeRate = new ExchangeRate(exchangeRate);

                addedExchangeRate.setId(generatedKeys.getLong(1));

                return addedExchangeRate;
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new DuplicateExchangeRateException();
            }
            throw new DatabaseException(e);
        }
    }

    public ExchangeRate updateExchangeRate(ExchangeRate exchangeRate) {
        String query = "UPDATE exchange_rate SET base_currency_id = ?, target_currency_id = ?, rate = ? WHERE id = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setLong(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setDouble(3, exchangeRate.getRate());
            preparedStatement.setLong(4, exchangeRate.getId());

            preparedStatement.executeUpdate();

            return new ExchangeRate(exchangeRate);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
