package com.bahmet.dao;

import com.bahmet.exception.DatabaseException;
import com.bahmet.exception.DuplicateCurrencyException;
import com.bahmet.model.Currency;
import com.bahmet.utils.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO {
    public List<Currency> getAllCurrencies() {
        List<Currency> currencies = new ArrayList<>();

        String query = "SELECT * FROM currency";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Currency currency = new Currency(
                        resultSet.getLong("id"),
                        resultSet.getString("code"),
                        resultSet.getString("fullname"),
                        resultSet.getString("sign"));

                currencies.add(currency);
            }

            return currencies;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public Currency getCurrencyByCode(String code) {
        String query = "SELECT * FROM currency WHERE code = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return null;
            } else {
                return new Currency(
                        resultSet.getLong("id"),
                        resultSet.getString("code"),
                        resultSet.getString("fullname"),
                        resultSet.getString("sign"));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public Currency addCurrency(Currency currency) {
        if (getCurrencyByCode(currency.getCode()) != null) {
            throw new DuplicateCurrencyException();
        }

        String query = "INSERT INTO currency(code, fullname, sign) VALUES (?, ?, ?)";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSymbol());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating Currency failed, no rows affected");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Creating Currency failed, no ID obtained");
                }

                Currency addedCurrency = new Currency(currency);

                addedCurrency.setId(generatedKeys.getLong(1));

                return addedCurrency;
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new DuplicateCurrencyException();
            }
            throw new DatabaseException(e);
        }
    }

    public Currency getCurrencyById(Long id) {
        String query = "SELECT * FROM currency WHERE id = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return null;
            } else {
                return new Currency(
                        resultSet.getLong("id"),
                        resultSet.getString("code"),
                        resultSet.getString("fullname"),
                        resultSet.getString("sign"));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
