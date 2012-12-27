import nose
from nose.tools import (assert_equals, assert_false, assert_not_equals,
                        assert_raises, assert_true, with_setup)

import os

from mercurial import util
from forest import (die_on_numeric_revs, relpath, urltopath)

TESTREPO = "/tmp"

def test_die_on_numeric_revs():
    die_on_numeric_revs(None)
    die_on_numeric_revs([""])
    die_on_numeric_revs(["000"])
    die_on_numeric_revs(["0000"])
    assert_raises(util.Abort, die_on_numeric_revs, ["0001"])
    assert_raises(util.Abort, die_on_numeric_revs, ["15"])
    die_on_numeric_revs(["0.9.4"])
    die_on_numeric_revs(["default"])
    die_on_numeric_revs(["tip"])


def test_relpath():
    assert_equals(relpath(TESTREPO, TESTREPO), '.')
    assert_equals(relpath(TESTREPO, TESTREPO + os.sep), '.')
    assert_equals(relpath(TESTREPO, os.path.join(TESTREPO, '.')), '.')
    assert_equals(relpath(TESTREPO, os.path.join(TESTREPO, '.hg', 'patches')),
                  os.path.join('.hg', 'patches'))


def test_urltopath():
    assert_equals(urltopath(None), None)
    assert_equals(urltopath(""), "")
    assert_equals(urltopath("foo"), "foo")
    assert_equals(urltopath("/tmp"), "/tmp")
    assert_equals(urltopath("file:tmp"), "tmp")
    assert_equals(urltopath("file:/tmp"), "/tmp")
    assert_equals(urltopath("file://tmp"), "tmp")
    assert_equals(urltopath("file:///tmp"), "/tmp")
    assert_equals(urltopath("http://localhost"), "http://localhost")
