/**
 * 
 */
package de.greenrobot.dao.query;

import de.greenrobot.dao.AbstractDao;

/**
 * @author martin.s.schumacher
 * @since 14.08.2013 17:25:38
 * 
 */
public abstract class AbstractEntityQueryBuilder<T> extends QueryBuilder<T> {

  protected AbstractEntityQueryBuilder(AbstractDao<T, ?> dao) {
    super(dao);
  }

  protected AbstractEntityQueryBuilder(AbstractDao<T, ?> dao, String tablePrefix) {
    super(dao, tablePrefix);
  }

  /**
   * <p>
   * creates a {@link Query} for <code>T</code> to find one by example.
   * <p>
   * Just nullable fields can be used.
   * 
   * @param example
   *          the entity filled with example values
   * 
   * @return a {@link Query} to find the example entity
   */
  public abstract Query<T> findByExample(T example);

}
