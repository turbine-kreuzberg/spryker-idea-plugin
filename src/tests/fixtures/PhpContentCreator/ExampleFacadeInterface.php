<?php

/**
 * Copyright © 2016-present Spryker Systems GmbH. All rights reserved.
 * Use of this software requires acceptance of the Evaluation License Agreement. See LICENSE file.
 */

namespace Spryker\Zed\Example\Business;

use Generated\Shared\Transfer\ExampleCollectionTransfer;

/**
 * @method \Spryker\Zed\Example\Business\ExampleBusinessFactory getFactory()
 * @method \Spryker\Zed\Example\Persistence\ExampleEntityManagerInterface getEntityManager()
 * @method \Spryker\Zed\Example\Persistence\ExampleRepositoryInterface getRepository()
 */
interface ExampleFacadeInterface
{
    /**
     * {@inheritDoc}
     *
     * @api
     *
     * @param \Generated\Shared\Transfer\ExampleCollectionTransfer $exampleCollectionTransfer
     *
     * @return \Generated\Shared\Transfer\ExampleCollectionTransfer
     */
    public function getExampleCollection(ExampleCollectionTransfer $exampleCollectionTransfer): ExampleCollectionTransfer;
}
