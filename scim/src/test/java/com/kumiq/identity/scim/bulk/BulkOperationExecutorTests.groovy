package com.kumiq.identity.scim.bulk

import com.kumiq.identity.scim.ScimApplication
import com.kumiq.identity.scim.database.InMemoryDatabase
import com.kumiq.identity.scim.database.ResourceDatabase.GroupDatabase
import com.kumiq.identity.scim.database.ResourceDatabase.UserDatabase
import com.kumiq.identity.scim.endpoint.support.BulkOpRequest
import com.kumiq.identity.scim.endpoint.support.BulkOpResponse
import com.kumiq.identity.scim.resource.constant.ScimConstants
import com.kumiq.identity.scim.resource.user.ScimUser
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import javax.annotation.Resource

/**
 *
 *
 * @author Weinan Qiu
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(classes = [ScimApplication])
class BulkOperationExecutorTests {

    @Resource(name = 'bulkOperationExecutor')
    BulkOperationExecutor bulkOperationExecutor

    @Autowired
    UserDatabase userDatabase

    @Autowired
    GroupDatabase groupDatabase

    @Before
    void setup() {
        ScimUser user = new ScimUser(id: 'user1', userName: 'user1')
        userDatabase.save(user)
    }

    @After
    void cleanUp() {
        (userDatabase as InMemoryDatabase.UserInMemoryDatabase).reset()
    }

    @Test
    void testCreateUser() {
        BulkOpRequest.Operation operation = new BulkOpRequest.Operation(
                method: HttpMethod.POST,
                bulkId: 'qwerty',
                path: '/Users',
                jsonData: [
                        'schemas': [ScimConstants.URN_USER],
                        'userName': 'davidiamyou'
                ]
        )

        BulkOpResponse.Operation response = bulkOperationExecutor.execute(operation)

        Assert.assertEquals(HttpStatus.CREATED, response.httpStatus)
        Assert.assertNotNull(response.location)
        Assert.assertNotNull(response.version)
        Assert.assertNotNull(response.response)
    }
}
