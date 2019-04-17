package tech.simter.operation.dao.jpa

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.test.test
import tech.simter.operation.dao.OperationDao
import tech.simter.operation.dao.jpa.TestHelper.randomString
import tech.simter.operation.po.Attachment
import tech.simter.operation.po.Field
import tech.simter.operation.po.Operation
import tech.simter.operation.po.Operator

/**
 * Test [OperationDaoImpl.get].
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@DataJpaTest
class GetMethodImplTest @Autowired constructor(
  private val repository: OperationJpaRepository,
  private val dao: OperationDao
) {
  private fun randomOperation(
    attachments: List<Attachment>? = null,
    fields: List<Field>? = null
  ): Operation {
    return Operation(
      type = randomString(),
      operator = Operator(
        id = "${++i}",
        name = randomString()
      ),
      target = tech.simter.operation.po.Target(
        id = "${++i}",
        type = randomString(),
        name = randomString()
      ),
      attachments = attachments,
      fields = fields
    )
  }

  private var i = 0
  private fun randomAttachment(): Attachment {
    return Attachment(
      id = "${++i}",
      name = randomString(),
      ext = "ext",
      size = ++i
    )
  }

  private fun randomField(nullOld: Boolean = false, nullNew: Boolean = false): Field {
    return Field(
      id = "${++i}",
      name = randomString(),
      type = "String",
      oldValue = if (nullOld) null else randomString(),
      newValue = if (nullNew) null else randomString()
    )
  }

  @Test
  fun `get existent data`() {
    // init data
    val expected = repository.saveAndFlush(randomOperation())

    // invoke and verify
    dao.get(expected.id).test().expectNext(expected).verifyComplete()
  }

  @Test
  fun `get existent data with attachment`() {
    // init data
    val expected = repository.saveAndFlush(randomOperation(
      attachments = listOf(randomAttachment(), randomAttachment())
    ))

    // invoke and verify
    dao.get(expected.id).test().expectNext(expected).verifyComplete()
  }

  @Test
  fun `get existent data with fields`() {
    // init data
    val expected = repository.saveAndFlush(randomOperation(
      fields = listOf(randomField(nullOld = true), randomField(nullNew = true))
    ))

    // invoke and verify
    dao.get(expected.id).test().expectNext(expected).verifyComplete()
  }

  @Test
  fun `get nonexistent data`() {
    dao.get(randomString()).test().expectComplete().verify()
  }
}