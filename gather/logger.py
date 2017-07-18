# -*- coding: utf-8 -*-
"""
    logger.py
    ~~~~~~~~~~~~~~
    something here

    :copyright: (c) 2017 by wang.mingyu.
"""
import logging
FORMAT = '%(asctime)s - %(name)s - %(levelname)s - %(message)s'
formatter = logging.Formatter(FORMAT)
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)

file_handler = logging.FileHandler('osmap_search.log')
file_handler.setFormatter(formatter)
file_handler.setLevel(logging.DEBUG)
logger.addHandler(file_handler)

# console_handler = logging.StreamHandler()
# console_handler.setFormatter(formatter)
# console_handler.setLevel(logging.DEBUG)
# logger.addHandler(console_handler)

