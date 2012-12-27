import nose
from nose.tools import (assert_equals, assert_false, assert_not_equals,
                        assert_raises, assert_true, with_setup)

import os
from subprocess import (Popen, PIPE)
import shutil
import sys

from mercurial import (hg, localrepo, ui, util)
from mercurial.repo import RepoError
from forest import (Forest, relpath)
from forest import (die_on_numeric_revs, relpath, urltopath, Forest)

TESTREPO = os.path.join(os.path.dirname(__file__), 'repo')


def run(cmd, cwd=None):
    print "$", " ".join(cmd)
    p = Popen(cmd, cwd=cwd, stdout=PIPE, stderr=PIPE)
    output = p.communicate()[0]
    run.returncode = p.returncode
    sys.stdout.write(output)
    return output


def create_repo():
    paths = [TESTREPO,
             os.path.join(TESTREPO, 'a'),
             os.path.join(TESTREPO, 'b'),
             os.path.join(TESTREPO, 'c')]
    if os.path.isdir(TESTREPO):
        shutil.rmtree(TESTREPO)
    for path in paths:
        os.mkdir(path)
        run(["hg", "init"], cwd=path)
        run(["hg", "qinit", "-c"], cwd=path)
    fd = open(os.path.join(TESTREPO, "README"), 'w')
    print >>fd, "zero"
    fd.close()
    run(["hg", "add", "README"], cwd=TESTREPO)
    run(["hg", "ci", "-m", "zero"], cwd=TESTREPO)
    fd = open(os.path.join(TESTREPO, "README"), 'w')
    print >>fd, "one"
    fd.close()
    run(["hg", "add", "README"], cwd=TESTREPO)
    run(["hg", "ci", "-m", "one"], cwd=TESTREPO)
    fd = open(os.path.join(TESTREPO, "README"), 'w')
    print >>fd, "two"
    fd.close()
    run(["hg", "add", "README"], cwd=TESTREPO)
    run(["hg", "ci", "-m", "two"], cwd=TESTREPO)


@with_setup(create_repo)
def test_Tree_init():
    top = hg.repository(None, TESTREPO)

    tree = Forest.Tree()
    assert_equals(tree.repo, None)
    assert_equals(tree.root, None)
    assert_equals(tree.revs, [])
    assert_equals(tree.paths, {})

    tree = Forest.Tree(repo=top)
    assert_equals(tree.repo, top)
    assert_equals(tree.root, TESTREPO)
    assert_equals(tree.revs, [])
    assert_equals(tree.paths, {})


@with_setup(create_repo)
def test_Tree_die_on_mq():
    forest = Forest(top=hg.repository(None, TESTREPO))
    tree = forest.top()
    tree.die_on_mq()
    try:
        os.unlink(os.path.join(TESTREPO, ".hg", "patches", "status"))
    except OSError:
        pass
    tree.die_on_mq()
    print run(["hg", "qnew", "patch"], cwd=TESTREPO)
    try:
        tree.die_on_mq()
    except util.Abort, err:
        assert_equals(str(err), "'%s' has mq patches applied" % TESTREPO)
    print run(["hg", "qpop", "-a"], cwd=TESTREPO)
    tree.die_on_mq()
    print run(["hg", "qpush", "-a"], cwd=TESTREPO)
    try:
        tree.die_on_mq(tree)
    except util.Abort, err:
        assert_equals(str(err), "'.' has mq patches applied")


@with_setup(create_repo)
def test_Tree_mq_applied():
    forest = Forest(top=hg.repository(None, TESTREPO))
    tree = forest.top()
    assert_false(tree.mq_applied())
    try:
        os.unlink(os.path.join(TESTREPO, ".hg", "patches", "status"))
    except OSError:
        pass
    assert_false(tree.mq_applied())
    print run(["hg", "qnew", "patch"], cwd=TESTREPO)
    assert_true(tree.mq_applied())
    print run(["hg", "qpop", "-a"], cwd=TESTREPO)
    assert_false(tree.mq_applied())
    print run(["hg", "qpush", "-a"], cwd=TESTREPO)
    assert_true(tree.mq_applied())


@with_setup(create_repo)
def test_Tree_getpath():
    forest = Forest(top=hg.repository(None, TESTREPO))
    tree = forest.top()
    assert_equals(tree.getpath(None), None)
    assert_equals(tree.getpath(["/tmp"]), "/tmp")
    assert_equals(tree.getpath(["file:///tmp"]), "/tmp")
    assert_equals(tree.getpath(["file:/tmp"]), "/tmp")
    assert_equals(tree.getpath(["hg://localhost"]), "hg://localhost")
    assert_equals(tree.getpath(["http://localhost"]), "http://localhost")
    assert_equals(tree.getpath(["https://localhost"]), "https://localhost")
    assert_equals(tree.getpath(["old-http://localhost"]), "old-http://localhost")
    assert_equals(tree.getpath(["static-http://localhost"]), "static-http://localhost")
    assert_equals(tree.getpath(["ssh://localhost"]), "ssh://localhost")
    assert_equals(tree.getpath(["default"]), None)
    fd = open(os.path.join(TESTREPO, ".hg", "hgrc"), "a")
    fd.write("\n"
             "[paths]\n"
             "default = /tmp\n"
             "default-push = http://localhost\n"
             "simon = file:///tmp\n")
    fd.close()
    assert_equals(tree.getpath(["default"]), None)
    print "tree.paths =", tree.paths
    forest.update()
    print "tree.paths =", tree.paths
    assert_equals(tree.getpath(["default"]), "/tmp")
    assert_equals(tree.getpath(["simon"]), "/tmp")
    assert_equals(tree.getpath(["default-push"]), "http://localhost")
    assert_equals(tree.getpath(["law", "default-push", "default"]),
                               "http://localhost")
    

@with_setup(create_repo)
def test_Tree_repo():
    tree = Forest.Tree(root=TESTREPO)
    assert_raises(AttributeError, tree.getrepo)
    repo = tree.getrepo(None)
    assert_true(isinstance(repo, localrepo.localrepository), type(repo))
    assert_equals(tree.root, TESTREPO)
    assert_equals(tree.revs, [])
    assert_equals(tree.paths, {})
    tree = Forest.Tree(root="")
    assert_equals(tree.repo, None)
    assert_equals(tree.root, "")
    top = hg.repository(None, TESTREPO)
    tree.repo = top
    assert_equals(tree.repo, top)
    assert_equals(tree.root, TESTREPO)
    tree.root = TESTREPO
    try:
        tree.repo
    except AttributeError, err:
        assert_equals(str(err), "getrepo() requires 'ui' parameter")
    assert_equals(tree.getrepo(None).root, TESTREPO)
    assert_equals(tree.root, TESTREPO)
    tree.root = "bad-repo"
    try:
        tree.repo
    except AttributeError, err:
        assert_equals(str(err), "getrepo() requires 'ui' parameter")
    try:
        tree.getrepo(None).root
    except RepoError, err:
        assert_equals(str(err), "repository bad-repo not found")
    assert_equals(tree.root, "bad-repo")


@with_setup(create_repo)
def test_Tree_root():
    top = hg.repository(None, TESTREPO)
    tree = Forest.Tree(repo=top)
    assert_equals(tree.repo, top)
    assert_equals(tree.root, TESTREPO)
    tree.root = ""
    assert_equals(tree.repo, None)
    assert_equals(tree.root, "")
    tree.root = None
    assert_equals(tree.repo, None)
    assert_equals(tree.root, None)


def test_Tree_skip():
    @Forest.Tree.skip
    def failing(message):
        raise Exception(message)
    assert_raises(Forest.Tree.Skip, failing)
    assert_raises(Forest.Tree.Skip, failing, "message")
    try:
        failing("hello")
    except Exception, message:
        assert_equals(str(message), "hello")


def test_Tree_warn():
    @Forest.Tree.warn
    def warning(message):
        raise Exception(message)
    assert_raises(Warning, warning)
    assert_raises(Warning, warning, "message")
    try:
        warning("hello")
    except Exception, message:
        assert_equals(str(message), "hello")
    pass


@with_setup(create_repo)
def test_Tree_working_revs():
    forest = Forest(top=hg.repository(None, TESTREPO))
    print "forest =", forest, "\n"
    rev = run(["hg", "identify", "--debug"], cwd=TESTREPO)
    print rev
    rev = rev.split()[0]
    assert_equals(forest.trees[0].working_revs(), [rev])
    assert_equals(forest.trees[1].working_revs(),
                  ['0000000000000000000000000000000000000000'])
    print run(["hg", "up", "0"], cwd=TESTREPO)
    rev = run(["hg", "identify", "--debug"], cwd=TESTREPO)
    print rev
    rev = rev.split()[0]
    forest.scan(walkhg=True)
    print "forest =", forest, "\n"
    assert_equals(forest.trees[0].working_revs(), [rev])
    assert_equals(forest.trees[1].working_revs(),
                  ['0000000000000000000000000000000000000000'])


def test_init():
    forest = Forest()
    print "forest =", forest
    assert_equals(forest.snapfile, None)
    assert_equals(forest.trees, [])


def test_apply():
    pass


def test_read():
    pass


@with_setup(create_repo)
def test_scan():
    user_interface = ui.ui()
    top = hg.repository(user_interface, TESTREPO)
    print "top.root =", top.root
    forest = Forest(top=top)
    print "forest =", forest
    forest.scan(walkhg=True)
    roots = [TESTREPO,
             os.path.join(TESTREPO, '.hg', 'patches'),
             os.path.join(TESTREPO, 'a'),
             os.path.join(TESTREPO, 'a', '.hg', 'patches'),
             os.path.join(TESTREPO, 'b'),
             os.path.join(TESTREPO, 'b', '.hg', 'patches'),
             os.path.join(TESTREPO, 'c'),
             os.path.join(TESTREPO, 'c', '.hg', 'patches')]
    assert_not_equals(forest.trees[0].repo, top)
    assert_equals(forest.trees[0].repo.root, roots[0])
    assert_equals(forest.trees[1].repo.root, roots[1])
    assert_equals(forest.trees[2].repo.root, roots[2])
    assert_equals(forest.trees[3].repo.root, roots[3])
    assert_equals(forest.trees[4].repo.root, roots[4])
    assert_equals(forest.trees[5].repo.root, roots[5])
    assert_equals(forest.trees[6].repo.root, roots[6])
    assert_equals(forest.trees[7].repo.root, roots[7])
    try:
        forest.trees[8].repo.root
    except Exception, err:
        assert_true(isinstance(err, IndexError))
    for tree in forest.trees:
        assert_equals(tree.repo.ui.parentui, user_interface)


@with_setup(create_repo)
def test_top():
    forest = Forest()
    assert_equals(forest.top(), None)

    top = hg.repository(None, TESTREPO)
    forest = Forest(top=top)
    assert_equals(forest.top().root, TESTREPO)


@with_setup(create_repo)
def test_update():
    forest = Forest(top=hg.repository(None, TESTREPO))
    print "forest =", forest, "\n"
    rev = run(["hg", "identify", "--debug"], cwd=TESTREPO)
    print rev
    rev = rev.split()[0]
    assert_equals(forest.top().working_revs(), [rev])
    print run(["hg", "up", "0"], cwd=TESTREPO)
    forest.update()
    print "forest =", forest, "\n"
    user_interface = ui.ui()
    forest.update(ui=user_interface)
    for tree in forest.trees:
        assert_equals(tree.repo.ui.parentui, user_interface)
    rev = run(["hg", "identify", "--debug"], cwd=TESTREPO)
    print rev
    rev = rev.split()[0]
    assert_equals(forest.trees[0].working_revs(), [rev])


def test_write():
    pass


