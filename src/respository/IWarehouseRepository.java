package respository;

import java.util.List;

import model.Phone;

public interface IWarehouseRepository {
    void save(Phone phone) throws Exception;

    void delete(String id);

    List<Phone> loadAll() throws Exception;
}
